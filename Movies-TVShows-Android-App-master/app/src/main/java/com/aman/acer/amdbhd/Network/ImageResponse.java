package com.aman.acer.amdbhd.Network;

import com.aman.acer.amdbhd.models.BackdropImage;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by KeshavAggarwal on 20/01/17.
 */

public class ImageResponse {
    @SerializedName("backdrops")
    private ArrayList<BackdropImage> bannerImageLinks;

    public ArrayList<BackdropImage> getBannerImageLinks() {
        return bannerImageLinks;
    }

    public void setBannerImageLinks(ArrayList<BackdropImage> bannerImageLinks) {
        this.bannerImageLinks = bannerImageLinks;
    }
}
