package com.SmartAlbumWithCloud.view;

import android.widget.GridView;

public class MyGridView extends GridView
{  
    public MyGridView(android.content.Context context,  
            android.util.AttributeSet attrs)  
    {  
        super(context, attrs);  
    }  

  /** 
     * Setting does not scroll
     */  
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
    {  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
  
    }  
  
}  