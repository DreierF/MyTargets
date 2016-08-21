package de.dreier.mytargets.interfaces;

public interface ItemAdapter<T> {
    void notifyDataSetChanged();
    void removeItem(T item);
    void addItem(T item);
    T getItemById(long id);
}
