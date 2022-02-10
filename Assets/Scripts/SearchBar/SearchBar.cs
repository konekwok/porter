﻿using Unity.UIWidgets.widgets;
public partial class SearchBar : StatefulWidget
{
    public SearchBar(
        SearchBarStyle style = SearchBarStyle.normal,
        DropDownOverlayType filterDropDownOverlayType = DropDownOverlayType.scrollable)
    {
        _style = style;
        _filterDropDownOverlayType = filterDropDownOverlayType;
    }

    private readonly SearchBarStyle _style;

    private readonly DropDownOverlayType _filterDropDownOverlayType;

    public const float Height = 70f;

    public override State createState() => new SearchBarState();
}