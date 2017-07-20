package dk.trustworks.network;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Created by hans on 08/07/2017.
 */
public class FooRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        System.out.println("FooRequestInterceptor.apply");
        System.out.println("requestTemplate = [" + requestTemplate + "]");
        System.out.println("requestTemplate = [" + requestTemplate.queryLine() + "]");
    }
}
