/*
 * Iris is a World Generator for Minecraft Bukkit Servers
 * Copyright (c) 2021 Arcane Arts (Volmit Software)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.volmit.iris.engine.headless;

import com.volmit.iris.Iris;
import com.volmit.iris.core.IrisDataManager;
import com.volmit.iris.engine.framework.EngineCompositeGenerator;
import com.volmit.iris.engine.object.IrisDimension;
import com.volmit.iris.engine.object.common.IrisWorld;
import com.volmit.iris.util.plugin.VolmitSender;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;

@Data
@SuppressWarnings("ResultOfMethodCallIgnored")
public class HeadlessWorld {
    private final IrisDimension dimension;
    private final String worldName;
    private final IrisWorld world;

    public HeadlessWorld(String worldName, IrisDimension dimension, long seed)
    {
        this.worldName = worldName;
        this.dimension = dimension;
        world = IrisWorld.builder()
                .environment(dimension.getEnvironment())
                .worldFolder(new File(worldName))
                .seed(seed)
                .maxHeight(256)
                .minHeight(0)
                .name(worldName)
                .build();
        world.worldFolder().mkdirs();
        new File(world.worldFolder(), "region").mkdirs();

        if(!new File(world.worldFolder(), "iris").exists())
        {
            Iris.proj.installIntoWorld(new VolmitSender(Bukkit.getConsoleSender(), Iris.instance.getTag("Headless")), dimension.getLoadKey(), world.worldFolder());
        }
    }

    public HeadlessGenerator headlessMode()
    {
        return new HeadlessGenerator(this);
    }

    public World load()
    {
        return new WorldCreator(worldName)
                .environment(dimension.getEnvironment())
                .seed(world.seed())
                .generator(new EngineCompositeGenerator(dimension.getLoadKey(), true))
                .createWorld();
    }

    public static HeadlessWorld from(String name, String dimension, long seed)
    {
        return new HeadlessWorld(name, IrisDataManager.loadAnyDimension(dimension), seed);
    }
}
