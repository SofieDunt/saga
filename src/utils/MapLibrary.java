package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Represents an {@link Library} that keeps track of its records in a map.
 *
 * @param <K> the type of record in the library
 */
public class MapLibrary<K> implements Library<K> {

  private final Map<String, K> library;
  private final UnaryOperator<String> nullMsg;
  private final UnaryOperator<String> missingMsg;
  private final UnaryOperator<String> conflictMsg;

  /**
   * Constructs an empty {@code MapLibrary} that throws exceptions with the given messages. Uses
   * default arguments for null operators.
   *
   * @param nullMsg     produces the message to throw when a required argument is null
   * @param missingMsg  produces the message to throw when a queried record does not exist from the
   *                    queried name
   * @param conflictMsg produces the message to throw when a name is invalid from the name, i.e.
   *                    conflicts with an existing name
   */
  public MapLibrary(UnaryOperator<String> nullMsg, UnaryOperator<String> missingMsg,
      UnaryOperator<String> conflictMsg) throws IllegalArgumentException {
    UnaryOperator<String> defaultOp = s -> s;
    if (nullMsg == null) {
      nullMsg = defaultOp;
    }
    if (missingMsg == null) {
      missingMsg = defaultOp;
    }
    if (conflictMsg == null) {
      conflictMsg = defaultOp;
    }

    this.library = new HashMap<>();
    this.nullMsg = nullMsg;
    this.missingMsg = missingMsg;
    this.conflictMsg = conflictMsg;
  }

  @Override
  public void add(String name, K record) throws IllegalArgumentException {
    Utils.ensureNotNull(record, nullMsg.apply("Can't be null"));
    this.library.put(createValidName(name), record);
  }

  @Override
  public void remove(String name) throws IllegalArgumentException {
    ensureRecordExists(name);
    this.library.remove(name);
  }

  @Override
  public K retrieve(String name) throws IllegalArgumentException {
    return ensureRecordExists(name);
  }

  @Override
  public void rename(String current, String newName) throws IllegalArgumentException {
    ensureRecordExists(current);
    if (newName != null && !this.library.containsKey(newName)) {
      K record = this.library.remove(current);
      this.library.put(newName, record);
    } else {
      throw new IllegalArgumentException(this.conflictMsg.apply(newName));
    }
  }

  @Override
  public void update(String name, K record) throws IllegalArgumentException {
    Utils.ensureNotNull(record, nullMsg.apply("Can't be null"));
    ensureRecordExists(name);
    this.library.replace(name, record);
  }

  @Override
  public List<String> getAllNames() {
    List<String> names = new ArrayList<>(this.library.keySet());
    Collections.sort(names);
    return names;
  }

  /**
   * Creates a valid name (unique in the library) from the given name by adding a number after it.
   *
   * @param preferredName the given name
   * @return the valid name
   */
  private String createValidName(String preferredName) {
    if (preferredName == null) {
      preferredName = "Untitled";
    }

    String validName = preferredName;
    int increment = 0;
    while (this.library.containsKey(validName)) {
      increment++;
      validName = preferredName + "(" + increment + ")";
    }

    return validName;
  }

  /**
   * Ensures the named record exists in the library.
   *
   * @param name the name of the record
   * @return the record of the given name
   * @throws IllegalArgumentException if no record of the given name exists, or the name is null
   */
  private K ensureRecordExists(String name) throws IllegalArgumentException {
    if (name != null && this.library.containsKey(name)) {
      return this.library.get(name);
    } else {
      throw new IllegalArgumentException(this.missingMsg.apply(name));
    }
  }
}
