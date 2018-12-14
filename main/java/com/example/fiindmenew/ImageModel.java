package com.example.fiindmenew;

import java.util.ArrayList;

public class ImageModel {
	public static ArrayList<ImageItem> Items;

    public static void LoadModel() {

        Items = new ArrayList<ImageItem>();
        Items.add(new ImageItem(1, "ic_food.png", "Food"));
        Items.add(new ImageItem(2, "ic_auto.png", "Auto"));
        Items.add(new ImageItem(3, "ic_entertainment.png", "Entertainment"));
        Items.add(new ImageItem(4, "ic_beauty.png", "Beauty"));
    }

    public static ImageItem GetbyId(int id){

        for(ImageItem item : Items) {
            if (item.Id == id) {
                return item;
            }
        }
        return null;
    }
}
