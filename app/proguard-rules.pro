# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-repackageclasses

# OrmLite
-keepclassmembers class com.example.weather.data.repository.dao.OrmLiteHelper {
    public <init>(android.content.Context);
}

-keepclassmembers class com.example.weather.data.model.WeatherFuture {
    public com.example.weather.data.model.WeatherCity weatherCity;
}