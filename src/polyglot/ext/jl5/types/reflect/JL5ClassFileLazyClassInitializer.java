package polyglot.ext.jl5.types.reflect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import polyglot.ext.jl5.types.JL5MethodInstance;
import polyglot.ext.jl5.types.JL5ParsedClassType;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.ext.param.types.MuPClass;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.types.reflect.*;
import polyglot.util.StringUtil;

/**
 * XXX TODO
 */
public class JL5ClassFileLazyClassInitializer extends ClassFileLazyClassInitializer {
    public JL5ClassFileLazyClassInitializer(ClassFile file, TypeSystem ts) {
        super(file, ts);
    }

    /**
     * Create the type for this class file.
     */
    @Override
    protected ParsedClassType createType() throws SemanticException {
        // The name is of the form "p.q.C$I$J".
        String name = clazz.classNameCP(clazz.getThisClass());

        if (Report.should_report(verbose, 2))
            Report.report(2, "creating ClassType for " + name);

        // Create the ClassType.
        JL5ParsedClassType ct = (JL5ParsedClassType) ts.createClassType(this);
        ct.flags(ts.flagsForBits(clazz.getModifiers()));
        ct.position(position());

        // This is the "p.q" part.
        String packageName = StringUtil.getPackageComponent(name);

        // Set the ClassType's package.
        if (!packageName.equals("")) {
            ct.package_(ts.packageForName(packageName));
        }

        // This is the "C$I$J" part.
        String className = StringUtil.getShortNameComponent(name);

        String outerName; // This will be "p.q.C$I"
        String innerName; // This will be "J"

        outerName = name;
        innerName = null;

        while (true) {
            int dollar = outerName.lastIndexOf('$');

            if (dollar >= 0) {
                outerName = name.substring(0, dollar);
                innerName = name.substring(dollar + 1);
            }
            else {
                outerName = name;
                innerName = null;
                break;
            }

            // Try loading the outer class.
            // This will recursively load its outer class, if any.
            try {
                if (Report.should_report(verbose, 2))
                    Report.report(2, "resolving " + outerName + " for " + name);
                ct.outer(this.typeForName(outerName));
                break;
            }
            catch (SemanticException e) {
                // Failed. The class probably has a '$' in its name.
                if (Report.should_report(verbose, 3))
                    Report.report(2, "error resolving " + outerName);
            }
        }

        ClassType.Kind kind = ClassType.TOP_LEVEL;

        if (innerName != null) {
            // A nested class. Parse the class name to determine what kind.
            StringTokenizer st = new StringTokenizer(className, "$");

            while (st.hasMoreTokens()) {
                String s = st.nextToken();

                if (Character.isDigit(s.charAt(0))) {
                    // Example: C$1
                    kind = ClassType.ANONYMOUS;
                }
                else if (kind == ClassType.ANONYMOUS) {
                    // Example: C$1$D
                    kind = ClassType.LOCAL;
                }
                else {
                    // Example: C$D
                    kind = ClassType.MEMBER;
                }
            }
        }

        if (Report.should_report(verbose, 3))
            Report.report(3, name + " is " + kind);

        ct.kind(kind);

        if (ct.isTopLevel()) {
            ct.name(className);
        }
        else if (ct.isMember() || ct.isLocal()) {
            ct.name(innerName);
        }

        // Add unresolved class into the cache to avoid circular resolving.
        ts.systemResolver().addNamed(name, ct);
        ts.systemResolver().addNamed(ct.fullName(), ct);

        //System.err.println("Added " + name + " " + ct + " to the system resolver.");

        JL5Signature signature = ((JL5ClassFile)clazz).getSignature();
        // Load the class signature
        //System.err.println("    signature == null? " + (signature == null));
        if (signature!=null)  {
            MuPClass pc = ((JL5TypeSystem)ts).mutablePClass(ct.position());
            ct.setPClass(pc);
            pc.clazz(ct);
            try {
                List<TypeVariable> typeVars = signature.parseClassTypeVariables(ts, position());
                ct.setTypeVariables(typeVars);
                pc.formals(new ArrayList(ct.typeVariables()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                signature.parseClassSignature(ts, position());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SemanticException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Set then read then set to force initialization of the classes
            // and interfaces so that they are initialized before we unify any
            // type variables
            ct.superType(signature.classSignature.superType());
            ct.setInterfaces(signature.classSignature.interfaces());

            ct.superType();
            ct.interfaces();

            ct.superType(signature.classSignature.superType());
            ct.setInterfaces(signature.classSignature.interfaces());

            /*System.err.println("Class signature type for " + name);
            System.err.println("    interfaces " + signature.classSignature.interfaces());
            System.err.println("    supertype " + signature.classSignature.superType());
            System.err.println("           " + signature.classSignature.superType().getClass());
            System.err.println("    typevars " + signature.classSignature.typeVars());
            System.err.println("  type vars " + signature.classSignature.typeVars() + " for class " + name);

            System.err.println("Class signature type for " + name);
            System.err.println("          " + ct.getClass());
            System.err.println("    interfaces " + ct.interfaces());
            System.err.println("    supertype " + ct.superType());
            System.err.println("           " + ct.superType().getClass());
            System.err.println("    typevars " + ct.typeVariables());
            System.err.println("  type vars " + ct.typeVariables() + " for class " + name);*/
        }
        else {
            //System.err.println("Class signature type for " + name + ": null signature");
        }

        return ct;
    }

    @Override
    protected MethodInstance methodInstance(Method method_, ClassType ct) {
        JL5Method method = (JL5Method) method_;
        Constant[] constants = clazz.getConstants();
        String name = (String) constants[method.getName()].value();
        String type = (String) constants[method.getType()].value();
        JL5Signature signature = method.getSignature();
        
        List excTypes = new ArrayList();

        // JL5 method signature does not contain the throw types
        // so parse that first, so we can use it in both cases.
        Exceptions exceptions = method.getExceptions();
        if (exceptions != null) {
            int[] throwTypes = exceptions.getThrowTypes();
            for (int i = 0; i < throwTypes.length; i++) {
                String s = clazz.classNameCP(throwTypes[i]);
                excTypes.add(quietTypeForName(s));
            }
        }

        if (signature!=null)  {
            try {
                signature.parseMethodSignature(ts, position(), ct);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SemanticException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Type jl5RetType = signature.methodSignature.returnType;
            
            List tt = signature.methodSignature.throwTypes();
            if (tt != null && !tt.isEmpty()) {
                // be robust in case for some reason the signature did include throw types info
                excTypes = tt;
            }
            
            /*System.err.println("Method signature type for " + name);
            System.err.println("    returnType " +jl5RetType);
            System.err.println("    formalTypes " +signature.methodSignature.formalTypes);
            System.err.println("    typevars " +signature.methodSignature.typeVars);
            System.err.println("    throwTypes " +excTypes);
             */
            return ((JL5TypeSystem)ts).methodInstance(ct.position(), ct,
                                                      ts.flagsForBits(method.getModifiers()),
                                                      signature.methodSignature.returnType(), 
                                                      name, 
                                                      signature.methodSignature.formalTypes(), 
                                                      excTypes,
                                                      signature.methodSignature.typeVars());
        }
        else {
            // System.err.println("Method signature type for " + name + " returnType: null signature");

            if (type.charAt(0) != '(') {
                throw new ClassFormatError("Bad method type descriptor.");
            }

            int index = type.indexOf(')', 1);
            List argTypes = typeListForString(type.substring(1, index));
            Type returnType = typeForString(type.substring(index+1));


            return ((JL5TypeSystem)ts).methodInstance(ct.position(), ct,
                                                      ts.flagsForBits(method.getModifiers()),
                                                      returnType, 
                                                      name, 
                                                      argTypes, 
                                                      excTypes);
        }
    }

    @Override
    protected ConstructorInstance constructorInstance(Method method,
                                                      ClassType ct, Field[] fields) {
        // Get a method instance for the <init> method.
        JL5MethodInstance mi = (JL5MethodInstance) methodInstance(method, ct);

        List formals = mi.formalTypes();

        if (ct.isInnerClass()) {
            // If an inner class, the first argument may be a reference to an
            // enclosing class used to initialize a synthetic field.

            // Count the number of synthetic fields.
            int numSynthetic = 0;

            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isSynthetic()) {
                    numSynthetic++;
                }
            }

            // Ignore a number of parameters equal to the number of synthetic
            // fields.
            if (numSynthetic <= formals.size()) {
                formals = formals.subList(numSynthetic, formals.size());
            }
        }

        return ((JL5TypeSystem)ts).constructorInstance(mi.position(), ct, mi.flags(),
                                                       formals, mi.throwTypes(), mi.typeParams());
    }

    @Override
    protected FieldInstance fieldInstance(Field field_, ClassType ct) {
        JL5Field field = (JL5Field) field_;
        Constant[] constants = clazz.getConstants();
        String name = (String) constants[field.getName()].value();
        String type = (String) constants[field.getType()].value();

        FieldInstance fi = null;
        JL5Signature signature = field.getSignature();
        if (signature != null) {
            try {
                signature.parseFieldSignature(ts, position(), ct);
                Type jl5FieldType = signature.fieldSignature.type;
                //System.err.println("Field type for " + name + " is " +jl5FieldType);
                fi = ts.fieldInstance(ct.position(), ct,
                                      ts.flagsForBits(field.getModifiers()),
                                      signature.fieldSignature.type, name);
            } catch (IOException e1) {
                e1.printStackTrace();
                fi = ts.fieldInstance(ct.position(), ct, ts.flagsForBits(field.getModifiers()), typeForString(type), name);
            } catch (SemanticException e1) {
                e1.printStackTrace();
                fi = ts.fieldInstance(ct.position(), ct, ts.flagsForBits(field.getModifiers()), typeForString(type), name);
            }
        } else {
            fi = ts.fieldInstance(ct.position(), ct, ts.flagsForBits(field.getModifiers()), typeForString(type), name);
        }

        if (field.isConstant()) {
            Constant c = field.constantValue();

            Object o = null;

            try {
                switch (c.tag()) {
                    case Constant.STRING: o = field.getString(); break;
                    case Constant.INTEGER: o = new Integer(field.getInt()); break;
                    case Constant.LONG: o = new Long(field.getLong()); break;
                    case Constant.FLOAT: o = new Float(field.getFloat()); break;
                    case Constant.DOUBLE: o = new Double(field.getDouble()); break;
                }
            }
            catch (SemanticException e) {
                throw new ClassFormatError("Unexpected constant pool entry.");
            }

            fi.setConstantValue(o);
            return fi;
        }
        else {
            fi.setNotConstant();
        }

        return fi;
    }



}