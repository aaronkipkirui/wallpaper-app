package com.kemoi.kvwallpaper.Listeners;

import com.kemoi.kvwallpaper.Models.CuratedApiResponse;

public interface CuratedResponseListener {
    void onFetch(CuratedApiResponse response, String message);
    void onError(String message);
}
