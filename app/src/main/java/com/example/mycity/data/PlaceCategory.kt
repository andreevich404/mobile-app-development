package com.example.mycity.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.mycity.R

enum class PlaceCategory(
    @StringRes val title: Int,
    @StringRes val shortTitle: Int,
    @DrawableRes val icon: Int
) {
    Restaurants(R.string.category_restaurant, R.string.category_restaurant_short, R.drawable.ic_category_restaurant),
    Parks(R.string.category_park, R.string.category_park_short, R.drawable.ic_category_park),
    Cafes(R.string.category_cafe, R.string.category_cafe_short, R.drawable.ic_category_cafe),
    Cinemas(R.string.category_cinema, R.string.category_cinema_short, R.drawable.ic_category_cinema),
    Malls(R.string.category_mall, R.string.category_mall_short, R.drawable.ic_category_mall)
}
