package com.falsepattern.ssmlegacy.mixin.plugin;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.always;


@RequiredArgsConstructor
public enum Mixin implements IMixin {
    //region fml->client
        GuiScrollingListMixin(Side.CLIENT, always(), "fml.GuiScrollingListMixin"),
    //endregion
    ;

    @Getter
    public final Side side;
    @Getter
    public final Predicate<List<ITargetedMod>> filter;
    @Getter
    public final String mixin;
}
