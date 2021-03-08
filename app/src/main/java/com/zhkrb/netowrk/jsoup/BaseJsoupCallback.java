/*
 * Copyright zhkrb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Create by zhkrb on 2019/10/27 0:29
 */

package com.zhkrb.netowrk.jsoup;

import android.text.TextUtils;
import android.util.Log;

import com.zhkrb.netowrk.BaseDataLoadCallback;
import com.zhkrb.netowrk.ExceptionUtil;
import com.zhkrb.netowrk.retrofit.manager.RequestManager;

import java.net.HttpURLConnection;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

public abstract class BaseJsoupCallback<T> implements Observer<ResponseBody>, BaseDataLoadCallback<T> {

    private String mTag;
    private Formatter mFormatter;

    public BaseJsoupCallback<T> addTag(String tag) {
        mTag = tag;
        return this;
    }

    @Override
    public void onSubscribe(Disposable d) {
        RequestManager.getInstance().add(mTag, d);
        onStart();
    }

    @Override
    public void onNext(@NonNull ResponseBody responseBody) {
        try {
            BufferedSource bufferedSource = Okio.buffer(responseBody.source());
            String response = bufferedSource.readUtf8();
            bufferedSource.close();
            if (mFormatter != null) {
                mFormatter.format(BaseJsoupCallback.this, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        ExceptionUtil.Msg msg1 = ExceptionUtil.getException(e);
        Log.e("Jsoup exception", msg1.getCode() + ": " + msg1.getMsg());
        onError(msg1.getCode(), msg1.getMsg());
        RequestManager.getInstance().remove(mTag);
        onFinish();
    }


    public void onSuccess(T info) {
        onSuccess(HttpURLConnection.HTTP_OK, info == null ? "empty body" : "success", info);
    }

    @Override
    public void onComplete() {
        RequestManager.getInstance().remove(mTag);
        onFinish();
    }

    public BaseJsoupCallback<T> setFormatter(Formatter<T> formatter) {
        mFormatter = formatter;
        return this;
    }

    public interface Formatter<T> {

        /**
         * 格式化数据
         *
         * @param jsoupCallback
         * @param string
         * @throws Exception
         */
        void format(BaseJsoupCallback<T> jsoupCallback, String string) throws Exception;

    }
}
