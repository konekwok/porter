using System.Collections.Generic;
using Unity.UIWidgets.animation;
using Unity.UIWidgets.engine;
using Unity.UIWidgets.foundation;
using Unity.UIWidgets.material;
using Unity.UIWidgets.painting;
using Unity.UIWidgets.rendering;
using Unity.UIWidgets.ui;
using Unity.UIWidgets.widgets;
using UnityEngine;

public class UIMainPanel : UIWidgetsPanel
{
    protected override void OnEnable()
    {
        FontManager.instance.addFont(Resources.Load<Font>("Fonts/Brands"), "Brands");
        FontManager.instance.addFont(Resources.Load<Font>("Fonts/MaterialIcons-Regular"), "MaterialIcons");
        FontManager.instance.addFont(Resources.Load<Font>("Fonts/NotoSans"), "NotoSans");
        FontManager.instance.addFont(Resources.Load<Font>("Fonts/PingFangHeiTC-W4"), "PingFang");
        FontManager.instance.addFont(Resources.Load<Font>("Fonts/PingFangHeiTC-W6"), "PingFang", FontWeight.w500);
        Icons.LoadIconFont();
        base.OnEnable();
    }
    protected override Widget createWidget()
    {
        return new WidgetsApp
        (
            home: new MainApp(),
            pageRouteBuilder: ( RouteSettings settings, WidgetBuilder builder)=>{
                return new PageRouteBuilder(
                    settings:settings,
                    pageBuilder: (BuildContext context, Animation<float> animation, Animation<float> secondaryAnimation)=>builder(context)
                );
            }
        );
    }
}
class MainApp : StatefulWidget
{
    public MainApp(Key key = null):base(key)
    {
        
    }
    public override State createState()
    {
        return new MainState();
    }
}
class MainState : State<MainApp>
{
    int counter = 0;
    public override Widget build(BuildContext context)
    {
        var screenOverlay = new ScreenOverlay(
                child: new Column
                (
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children:new List<Widget>
                    {
                        new Header(),
                        new SearchBar(
                            filterDropDownOverlayType: DropDownOverlayType.builtin
                        )
                    }
                )
            );

            var defaultTextStyle = new DefaultTextStyle(
                child: screenOverlay,
                style: new TextStyle(
                    fontFamily: "PingFang"
                )
            );
        return defaultTextStyle;
    }
}
public class Header : StatelessWidget
    {
        public const float Height = 60f;

        public override Widget build(BuildContext context)
        {
            return new Container(
                height: Height,
                color: Colors.black
                // color: new Color(0xff000000)
            );
        }
    }
public static class FloatExtension
{
    private const float defaultFontSize = 16f;
    private const float defaultLineSpacing = 22.4f;

    public static float LineHeight(this float val) => defaultFontSize * val / defaultLineSpacing;
}