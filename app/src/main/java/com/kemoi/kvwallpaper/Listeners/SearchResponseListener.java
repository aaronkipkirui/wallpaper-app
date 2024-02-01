package com.kemoi.kvwallpaper.Listeners;

import com.kemoi.kvwallpaper.Models.SearchApiResponse;

public interface SearchResponseListener {
    void onFetch(SearchApiResponse response, String message);
    void onError(String message);
}
