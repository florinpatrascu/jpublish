package org.jpublish.module.wink.examples;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Memory store of bookmarks, created just for the needs of Bookmarks demo.
 * <p>
 * Memory store contains several predefined bookmarks. It is possible to list
 * all bookmarks, to get a particular bookmark and update an existing bookmark.
 * It is also possible to check whether the bookmark of a particular key exists.
 * <p>
 * BookmarkStore is singleton. The class is thread-safe.
 */
public class BookmarkStore {

    // --- singleton ---

    private static BookmarkStore             store     = new BookmarkStore();
    private static final Map<String, String> bookmarks =
                                                           Collections
                                                               .synchronizedMap(new HashMap<String, String>());
    private static int                       id        = 0;

    static {
        bookmarks.put("my-bookmark", "My demo bookmark");
        bookmarks.put(String.valueOf(++id), "First demo bookmark");
        bookmarks.put(String.valueOf(++id), "Second demo bookmark");
        bookmarks.put(String.valueOf(++id), "Third demo bookmark");
    }

    /**
     * Provides instance of this class (singleton)
     *
     * @return instance of this class
     */
    public static BookmarkStore getInstance() {
        return store;
    }

    /**
     * Constructor. Bookmarks are initialized with some data.
     */
    private BookmarkStore() {
    }

    /**
     * Gives hash map with all existing bookmarks and its keys.
     *
     * @return all existing bookmarks in memory store.
     */
    public Map<String, String> getBookmarks() {
        return Collections.unmodifiableMap(bookmarks);
    }

    /**
     * Updates or creates bookmark of given key with provided value.
     *
     * @param key bookmark key.
     * @param bookmark bookmark value.
     */
    public void putBookmark(String key, String bookmark) {
        bookmarks.put(key, bookmark);
    }

    /**
     * Gives bookmark of requested bookmark Id.
     *
     * @param key requested bookmark key (bookmark Id).
     * @return requested bookmark, or <tt>null</tt> if bookmark with such key
     *         does not exist.
     */
    public String getBookmark(String key) {
        return bookmarks.get(key);
    }

    /**
     * Returns <tt>true</tt> is memory store has bookmark for the provided key
     * (bookmark Id).
     *
     * @param key requested bookmark key (bookmark Id).
     * @return <tt>true</tt> if memory store has bookmark of the provided key.
     */
    public boolean containsBookmark(String key) {
        return bookmarks.containsKey(key);
    }

    /**
     * Generate Bookmark ID
     *
     * @return Bookmark ID
     */
    public static String getNewId() {
        return String.valueOf(++id);
    }

    /**
     * Delete bookmark of given key.
     *
     * @param key bookmark key.
     */
    public void deleteBookmark(String key) {
        bookmarks.remove(key);
    }

}
