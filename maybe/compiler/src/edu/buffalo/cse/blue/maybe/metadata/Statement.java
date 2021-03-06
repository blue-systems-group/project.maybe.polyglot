package edu.buffalo.cse.blue.maybe.metadata;

import polyglot.main.Main;
import polyglot.util.Position;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xcv58 on 9/15/15.
 */
public class Statement {
    private MaybeType type;
    private String label;
    private String content;
    private int line;
    private List<Alternative> alternatives;

    private transient List<Line> lineList;
    private transient int startLine;
    private transient int startColumn;
    private transient int endLine;
    private transient int endColumn;

    public Statement(Position position, Position label, List<Position> alternatives, MaybeType type) {
        this.type = type;
        this.line = position.line();
        this.content = this.getContent(position);
        this.label = this.getLabel(label);
        this.alternatives = new LinkedList<Alternative>();
        for (int i = 0; i < alternatives.size(); i++) {
            Alternative alternative = this.getAlternative(alternatives.get(i), i);
            this.alternatives.add(alternative);
        }
//        System.out.println(position);
//        System.out.println(content);
//        System.out.println();
    }

    public String getLabel() {
        return label;
    }

    private String getLabel(Position position) {
        int start = this.getLinearPosition(position.line() - 1, position.column()) + 1;
        int end = this.getLinearPosition(position.endLine() - 1, position.endColumn()) - 1;
        assert (start <= end);
        assert (end < content.length());
        return content.substring(start, end);
    }

    private Alternative getAlternative(Position position, int value) {
        int start = this.getLinearPosition(position.line() - 1, position.column());
        int end = this.getLinearPosition(position.endLine() - 1, position.endColumn());
        return new Alternative(start, end, value);
    }

    private int getLinearPosition(int line, int column) {
        if (line == startLine) {
            return column - startColumn;
        }
        int sum = lineList.get(startLine).length - startColumn + 1;
        for (int i = startLine + 1; i < line; i++) {
            sum += lineList.get(i).length + 1;
        }
        sum += column;
        return sum;
    }

    private String getContent(Position position) {
        try {
            lineList = this.getFileContent(position.path());
            startLine = position.line() - 1;
            startColumn = position.column();
            endLine = position.endLine() - 1;
            endColumn = position.endColumn();

            assert (startLine >= endLine);
            assert (endLine < lineList.size());
            if (startLine == endLine) {
                assert (endColumn > startColumn);
                String string = lineList.get(startLine).content;
                return string.substring(startColumn, endColumn);
            }

            StringBuilder stringBuilder = new StringBuilder();
            if (this.type == MaybeType.block) {
                // TODO: if the code style is not ordinary. Handle by reach the most left space/tab from startColumn.
                startColumn = 0;
            }
            stringBuilder.append(lineList.get(startLine).content.substring(startColumn));
            stringBuilder.append('\n');
            for (int i = startLine + 1; i < endLine; i++) {
                stringBuilder.append(lineList.get(i).content);
                stringBuilder.append('\n');
            }
            stringBuilder.append(lineList.get(endLine).content.substring(0, endColumn));
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new Main.TerminationException(e.getMessage());
        }
    }

    private List<Line> getFileContent(String path) throws IOException {
        assert (path != null);

        List<Line> content = new ArrayList<Line>();
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line;
        while ((line = reader.readLine()) != null) {
            content.add(new Line(line));
        }
        reader.close();
        return content;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(content);
        stringBuilder.append('\n');
        for (Alternative alternative : alternatives) {
            stringBuilder.append(alternative.value + ": ");
            stringBuilder.append(content.substring(alternative.start, alternative.end));
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
