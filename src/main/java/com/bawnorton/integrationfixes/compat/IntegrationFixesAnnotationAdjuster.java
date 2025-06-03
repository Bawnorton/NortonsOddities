package com.bawnorton.integrationfixes.compat;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.Inject;
import java.util.List;

public class IntegrationFixesAnnotationAdjuster implements MixinAnnotationAdjuster {
    @Override
    public AdjustableAnnotationNode adjust(List<String> targetClassNames, String mixinClassName, MethodNode handlerNode, AdjustableAnnotationNode annotationNode) {
        if(!"elocindev.item_obliterator.forge.mixin.VillagerTradeMixin".equals(mixinClassName)) return annotationNode;

        return annotationNode.is(Inject.class) ? null : annotationNode;
    }
}
