package com.org.app;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Base activity for all activities,which is created on app level
 * This is activity holds all common functionality for child activity.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Class<?> aClass;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (aClass != null && savedInstanceState != null)
            restoreInstance(savedInstanceState);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (aClass != null)
            holdInstance(outState);
    }


    private void restoreInstance(Bundle savedInstanceState) {
        ArrayList<String> annotationList = getAnnotationList();

        for (String str : annotationList) {
            Object obj = savedInstanceState.get(str);

            try {
                Field declaredField = aClass.getDeclaredField(str);
                declaredField.setAccessible(true);
                try {
                    declaredField.set(this, obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }


    private ArrayList<String> getAnnotationList() {
        Field[] fields = aClass.getDeclaredFields();

        ArrayList<String> annotationList = new ArrayList<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Keep.class)) {
                annotationList.add(f.getName());
            }
        }
        return annotationList;
    }

    private void holdInstance(Bundle outState) {
        ArrayList<String> annotationList = getAnnotationList();
        for (String str : annotationList) {
            try {
                Field declaredObj = aClass.getDeclaredField(str);
                declaredObj.setAccessible(true);
                try {
                    Object obj = declaredObj.get(this);
                    saveIntoBundle(outState, obj, str);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveIntoBundle(Bundle outState, Object obj, String key) {
        if (obj == null)
            return;

        if (obj instanceof Parcelable) {
            outState.putParcelable(key, (Parcelable) obj);
        } else if (obj instanceof Parcelable[]) {
            outState.putParcelableArray(key, (Parcelable[]) obj);
        } else if (obj instanceof String) {
            outState.putString(key, (String) obj);
        } else if (obj instanceof String[]) {
            outState.putStringArray(key, (String[]) obj);
        } else if (obj instanceof Integer) {
            outState.putInt(key, (Integer) obj);
        } else if (obj instanceof int[]) {
            outState.putIntArray(key, (int[]) obj);
        } else if (obj instanceof Boolean) {
            outState.putBoolean(key, (Boolean) obj);
        } else if (obj instanceof Double) {
            outState.putDouble(key, (Double) obj);
        } else if (obj instanceof double[]) {
            outState.putDoubleArray(key, (double[]) obj);
        } else if (obj instanceof Float) {
            outState.putFloat(key, (Float) obj);
        } else if (obj instanceof float[]) {
            outState.putFloatArray(key, (float[]) obj);
        } else if (obj instanceof List<?>) {
            holdListInstance(outState, obj, key);
        }
    }

    private void holdListInstance(Bundle outState, Object obj, String key) {

        List<Object> objList = null;
        if (obj instanceof List<?>) {
            try {
                objList = (ArrayList<Object>) obj;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (!objList.isEmpty()) {
                Object instanceObject = objList.get(0);

                if (instanceObject != null) {

                    if (instanceObject instanceof String) {
                        outState.putStringArrayList(key, (ArrayList<String>) obj);
                    } else if (instanceObject instanceof Integer) {
                        outState.putIntegerArrayList(key, (ArrayList<Integer>) obj);
                    } else if (instanceObject instanceof Parcelable) {
                        outState.putParcelableArrayList(key, (ArrayList<? extends Parcelable>) obj);
                    }
                }
            }
        }
    }
}
