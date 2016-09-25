/*
 * Copyright (C) 2015-2016 Fiete Wennier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Dieses Programm ist Freie Software: Sie können es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Wahl) jeder neueren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * Dieses Programm wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 *
 */

package com.github.fi3te.iliasdownloader.controller.courses;

import java.util.List;

import de.whiledo.iliasdownloader2.util.TwoObjectsX;

/**
 * Created by wennier on 12.09.2015.
 */
public abstract class AbstractDataProvider<G, C> {

    public abstract int getGroupCount();

    public abstract int getChildCount(int groupPosition);

    public abstract List<G> getGroupItems();

    public abstract G getGroupItem(int groupPosition);

    public abstract C getChildItem(int groupPosition, int childPosition);

    public abstract int addGroupItem(G groupItem);

    public abstract int addChildItem(int groupPosition, C childItem);

    public abstract TwoObjectsX<Integer, Integer> addChildItem(C childItem);

    public abstract TwoObjectsX<Integer, Integer> indexOfChildItem(C childItem);

    public abstract void updateChildItem(int groupPosition, int childPosition, C childItem);

    public abstract void removeGroupItem(int groupPosition);

    public abstract void removeChildItem(int groupPosition, int childPosition);

    public abstract void clear();

    public abstract void updateData(List<C> childItems);

}
