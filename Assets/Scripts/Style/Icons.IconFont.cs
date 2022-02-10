using Unity.UIWidgets.ui;
using UnityEngine;
using Unity.UIWidgets.widgets;


public static partial class Icons
{
    public static void LoadIconFont()
    {
        FontManager.instance.addFont(Resources.Load<Font>("Fonts/IconFont"), "IconFont");
    }
    
    
    public static readonly IconData IconFontCopy = new IconData(0xf102, fontFamily: "IconFont");
    
    public static readonly IconData IconFontLoading = new IconData(0xf101, fontFamily: "IconFont");
    
}