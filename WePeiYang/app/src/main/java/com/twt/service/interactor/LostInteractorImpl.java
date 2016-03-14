package com.twt.service.interactor;


import com.google.gson.JsonElement;
import com.twt.service.api.ApiClient;
import com.twt.service.bean.Lost;
import com.twt.service.bean.LostDetails;
import com.twt.service.support.PrefUtils;
import com.twt.service.ui.lostfound.lost.details.FailureEvent;
import com.twt.service.ui.lostfound.lost.details.SuccessEvent;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rex on 2015/8/2.
 */
public class LostInteractorImpl implements LostInteractor {

    @Override
    public void getLostList(int page) {
        ApiClient.getLostList(page, new Callback<Lost>() {
            @Override
            public void success(Lost lost, Response response) {
                EventBus.getDefault().post(new com.twt.service.ui.lostfound.lost.SuccessEvent(lost));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(new com.twt.service.ui.lostfound.lost.FailureEvent(error));
            }
        });
    }

    @Override
    public void getLostDetails(int id) {
        ApiClient.getLostDetails(id, new Callback<LostDetails>() {
            @Override
            public void success(LostDetails lostDetails, Response response) {
                EventBus.getDefault().post(new SuccessEvent(lostDetails));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(new FailureEvent(error));
            }
        });
    }

    @Override
    public void postLost(String title, String name, String time, String place, String phone, String content, String lost_type, String otherTag) {
        String authorization = PrefUtils.getToken();
        ApiClient.postLost(authorization, title, name, time, place, phone, content, lost_type, otherTag, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                EventBus.getDefault().post(new com.twt.service.ui.lostfound.post.lost.SuccessEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(new com.twt.service.ui.lostfound.post.lost.FailureEvent(error));
            }
        });
    }
}