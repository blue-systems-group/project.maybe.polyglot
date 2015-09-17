package edu.buffalo.cse.blue.maybe.metadata;

import com.google.gson.Gson;
import polyglot.main.Main;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.POST;

import java.io.IOException;


/**
 * Created by xcv58 on 9/16/15.
 */
public class Post {
    private interface MaybeMetadataService {
        @POST("maybe-api-v1/metadata")
        Call<PackageData> uploadMetadata(@Body PackageData packageData);
    }

    public void post(String url, PackageData packageData) throws IOException, Main.TerminationException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MaybeMetadataService service = retrofit.create(MaybeMetadataService.class);

        Call<PackageData> call = service.uploadMetadata(packageData);
        Response<PackageData> response = call.execute();
        if (response.isSuccess()) {
            // TODO: how to handle success, do we need notify user?
        } else {
            throw new Main.TerminationException(response.errorBody().string());
        }
    }
}
