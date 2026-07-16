package org.aussiebox.circuit_core.helper.item;

/// Allows you to register items before ItemGroups are populated.
public interface ItemSupplier {
    default void init() {

    }
}
