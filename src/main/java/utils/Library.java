package utils;

import java.util.List;

/**
 * An interface for a library that manages named records, collectively known as an entry.
 *
 * @param <K> the type of record in the library
 */
public interface Library<K> {

  /**
   * Adds the given record to the library under the given name. If the name already exists in the
   * library, creates a valid name.
   *
   * @param name   the name of the record to be added
   * @param record the record to be added
   * @throws IllegalArgumentException if the record is null
   */
  void add(String name, K record) throws IllegalArgumentException;

  /**
   * Removes the entry of the given name from the library.
   *
   * @param name the name of the entry
   * @throws IllegalArgumentException if no entry of the given name exists
   */
  void remove(String name) throws IllegalArgumentException;

  /**
   * Gets the record of the given name from the library.
   *
   * @param name the name of the record
   * @return the record
   * @throws IllegalArgumentException if no entry of the given name exists
   */
  K retrieve(String name) throws IllegalArgumentException;

  /**
   * Renames the entry of the given name in the library. Does not attempt to make the new name
   * valid.
   *
   * @param current the current name of the entry
   * @param newName the new name
   * @throws IllegalArgumentException if no entry of the given name exists or the new name is
   *                                  invalid (conflicts or is null)
   */
  void rename(String current, String newName) throws IllegalArgumentException;

  /**
   * Updates the entry of the given name to be associated with the given record.
   *
   * @param name   the name of the entry
   * @param record the record to update to
   * @throws IllegalArgumentException if the record is null or no entry of the given name exists
   */
  void update(String name, K record) throws IllegalArgumentException;

  /**
   * Gets a list of all entry names in the library in alphabetical order.
   *
   * @return the list of all entry names
   */
  List<String> getAllNames();
}
