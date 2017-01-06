/*
 * FlowDesign
 * Copyright (C) 2016 Tallbyte <copyright at tallbyte.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tallbyte.flowdesign.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-25)<br/>
 */
public class JointGroup<J extends Joint> {

    protected final String                      name;
    protected final Element                     element;

    protected final List<J>                     joints          = new ArrayList<>();
    protected final List<JointsChangedListener> listenersJoints = new ArrayList<>();
    protected final JointFactory<J>             factory;

    protected       int                         amount = 0;
    protected final int                         minJoints;
    protected final int                         maxJoints;

    protected final PropertyChangeSupport       changeSupport;

    public JointGroup(Element element, String name, int minJoints, int maxJoints, JointFactory<J> factory, int initial) {
        this.name      = name;
        this.element   = element;
        this.factory   = factory;
        this.minJoints = minJoints;
        this.maxJoints = maxJoints;

        this.changeSupport = new PropertyChangeSupport(this);

        for (int i = 0 ; i < initial && i < maxJoints ; ++i) {
            try {
                addJoint();
            } catch (JointGroupModifyException e) {
                throw new RuntimeException("initial joints could not be added?! this should never happen", e);
            }
        }
    }

    /**
     * Checks if {@link Joint}s can be added.
     *
     * @return Returns true if they can, else false.
     */
    public boolean canAdd() {
        return amount < maxJoints;
    }

    /**
     * Checks if {@link Joint}s can be removed.
     *
     * @return Returns true if they can, else false.
     */
    public boolean canRemove() {
        return amount > minJoints;
    }

    /**
     * Adds an {@link Joint}.
     *
     * @return Returns the created {@link Joint}.
     * @throws JointGroupModifyException Is thrown if no more {@link Joint}s can be added.
     */
    public J addJoint() throws JointGroupModifyException {
        if (!canAdd()) {
            throw new JointGroupModifyException("max joint amount exceeded");
        }

        J joint = factory.createJoint(element);
        joints.add(joint);
        setAmount(amount+1);

        for (JointsChangedListener listener : listenersJoints) {
            listener.onJointsChanged(joint, true);
        }

        return joint;
    }

    /**
     * Removes an {@link Joint}.
     *
     * @return Returns the removed {@link Joint}.
     * @throws JointGroupModifyException Is thrown if no more {@link Joint}s can be removed.
     */
    public J removeJoint() throws JointGroupModifyException {
        if (!canRemove()) {
            throw new JointGroupModifyException("min joint amount reached");
        }

        J joint = joints.remove(joints.size() - 1);

        for (JointsChangedListener listener : listenersJoints) {
            listener.onJointsChanged(joint, false);
        }

        return joint;
    }

    /**
     * Gets the {@link Joint} at the specified index.
     *
     * @param i the index
     * @return Returns the {@link Joint}.
     * @throws JointNotFoundException Is thrown if the index is unknown or out of range.
     */
    public J getJoint(int i) throws JointNotFoundException {
        if (i < 0 || i >= amount) {
            throw new JointNotFoundException("joint at location "+i+" could not be found");
        }

        return joints.get(i);
    }

    /**
     * Gets the index of a certain {@link Joint}.
     *
     * @param joint the {@link Joint} to check
     * @return Returns the index or -1 if the index does not belong to this {@link JointGroup}.
     */
    public int getIndex(J joint) {
        return joints.indexOf(joint);
    }

    /**
     * Gets the name.
     *
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets amount of {@link Joint}s.
     *
     * @param amount the new amount
     */
    private void setAmount(int amount) {
        int old = this.amount;
        this.amount = amount;
        changeSupport.firePropertyChange("amount", old, amount);
    }

    /**
     * Gets the amount of {@link Joint}s.
     *
     * @return Returns the amount.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Gets the minimum amount of {@link Joint}s.
     *
     * @return Returns the amount.
     */
    public int getMinJoints() {
        return minJoints;
    }

    /**
     * Gets the maximum amount of {@link Joint}s.
     *
     * @return Returns the amount.
     */
    public int getMaxJoints() {
        return maxJoints;
    }

    /**
     * Gets the list of {@link Joint}s.
     *
     * @return Returns the list.
     */
    public List<Joint> getJoints() {
        return Collections.unmodifiableList(joints);
    }

    /**
     * Registers an {@link JointsChangedListener}.
     * @param listener the {@link JointsChangedListener} to register
     */
    public void addJointsChangedListener(JointsChangedListener listener) {
        listenersJoints.add(listener);
    }

    /**
     * Unregisters an {@link JointsChangedListener}.
     * @param listener the {@link JointsChangedListener} to unregister
     */
    public void removeJointsChangedListener(JointsChangedListener listener) {
        listenersJoints.remove(listener);
    }

    /**
     * Registers an {@link PropertyChangeListener}.
     * @param listener the {@link PropertyChangeListener} to register
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Unregisters an {@link PropertyChangeListener}.
     * @param listener the {@link PropertyChangeListener} to unregister
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
