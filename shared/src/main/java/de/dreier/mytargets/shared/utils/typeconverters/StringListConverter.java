/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.shared.utils.typeconverters;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class StringListConverter extends TypeConverter<String, List<String>> {

    private static final String SEPARATOR = "!#!";

    @Override
    public String getDBValue(List<String> model) {
        if (model != null && !model.isEmpty()) {
            return Stream.of(model).collect(Collectors.joining(SEPARATOR));
        }

        return null;
    }

    @Override
    public List<String> getModelValue(String data) {
        if (data != null) {
            String[] split = data.split(SEPARATOR);
            return Arrays.asList(split);
        }

        return new ArrayList<>();
    }

}