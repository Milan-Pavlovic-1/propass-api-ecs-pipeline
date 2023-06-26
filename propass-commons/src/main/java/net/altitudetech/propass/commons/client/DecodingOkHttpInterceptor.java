package net.altitudetech.propass.commons.client;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DecodingOkHttpInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    String url = originalRequest.url().toString();
    // hack because retrofit's @Query(encoded=true) is not respected
    url = url.replaceAll("%26sort%3D", "&sort=");
    url = url.replaceAll("%26size%3D", "&size=");
    url = url.replaceAll("%2CASC", ",asc");
    url = url.replaceAll("%2CDESC", ",desc");
    url = url.replaceAll("%2Casc", ",asc");
    url = url.replaceAll("%2Cdesc", ",desc");

    Request newRequest = originalRequest.newBuilder().url(url).build();
    return chain.proceed(newRequest);
  }
}
