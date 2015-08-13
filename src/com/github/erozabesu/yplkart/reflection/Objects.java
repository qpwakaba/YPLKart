package com.github.erozabesu.yplkart.reflection;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class Objects {

    //〓 Nms 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Object nmsEnumTitleAction_PerformTitle = Classes.nmsEnumTitleAction.getEnumConstants()[0];
    public static Object nmsEnumTitleAction_PerformSubTitle = Classes.nmsEnumTitleAction.getEnumConstants()[1];
    public static Object nmsEnumClientCommand_PerformRespawn = Classes.nmsEnumClientCommand.getEnumConstants()[0];

    public static List<Object> nmsEnumParticle = (List<Object>) Arrays.asList(Classes.nmsEnumParticle.getEnumConstants());
}
