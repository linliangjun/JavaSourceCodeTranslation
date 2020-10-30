/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util;

import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Collection 接口是集合框架的根接口。集合代表一组对象，其中这些对象被称为：“元素”。
 * 有些集合允许有重复的元素，有些却不可以；有些集合的元素的顺序是确定的，有些却
 * 不是。JDK没有直接实现该接口，而是实现了它的子接口，例如：Set 和 List。该接口
 * 通常被用于传递集合对象，或者仅需最大通用性的地方。
 *
 * <p><i>Bags</i> or <i>multisets</i> (unordered collections that may contain
 * duplicate elements) should implement this interface directly.
 *
 * 该接口所有的通用实现类（通常是其子接口的实现类）都应该提供两个“标准”的构造方法：
 * 一是无参的构造方法，它会创建一个“空”集合；二是只有一个参数且类型为 Collection 
 * 的构造方法，它会创建一个与形参的元素相同的新集合。事实上，后者可以复制任何集合
 * 对象，从而产生等效的新集合。无法强制要求实现类都遵守此规范（因为接口中不能有
 * 构造方法），但是 Java 类库中所有的通用实现类都遵守了此规范。
 * 
 * <p>该接口中某些方法被标记为“可选的”，如果实现类不想实现这些方法，那么应该在
 * 方法上抛出 UnsupportedOperationException 异常。
 *
 * <p>实现类可能会对包含的元素加以限制，例如：有些实现类不允许元素为 null，而有些
 * 实现类对元素的类型有要求。尝试添加不合法的元素会抛出未检查的异常，这通常是 
 * NullPointerException 异常或 ClassCastException 异常；尝试查询不合法的元素可能
 * 会抛出异常，也可能只是简单地返回 false；实现类的行为要视具体情况而定。简单地说，
 * 操作不合法的元素时，并不会导致集合被非法插入，只是可能会抛出异常，这取决于
 * 实现类。因此，在该接口规范中，异常被标记为“可选的”。
 *
 * <p>集合的同步策略由其自身（实现类）决定。在没有同步策略的实现类对象中，其他
 * 线程调用的任何修改集合的方法都会产生未知的结果，这包括直接调用；将集合传递到
 * 一个可能会调用修改集合的方法的方法中；或是使用迭代器来检查集合。
 *
 * <p>集合框架中的许多方法都用到了 equals(Object) 方法，例如：contains(Object) 
 * 方法规范是：“当且仅当集合中至少包含一个元素 e，使得表达式
 * o==null ? e==null : o.equals(e) 的值为 ture 时，返回值为 true。”但这并不意
 * 味着一定要拘泥于 equals(Object) 方法，实现类还可以对其进行优化以避免调用 
 * equals(Object) 方法，例如：先比较两个对象的 hash 码（hashCode() 方法规范可以
 * 保证 hash 码不相等的两个对象不相等）。一般来说，只要实现者觉得合适，实现类就
 * 可以自由地调用 Object 中的方法。
 * 
 * <p>当集合直接或间接的将自身作为元素时，有些实现类的递归和遍历操作可能会失败并
 * 抛出引用自身实例的异常。出现这种情况的方法包括：clone()、equals()、hashCode()
 * 和 toString() 方法。实现类可以根据需求自行处理该问题，不过大部分实现类都没有
 * 处理该问题。
 *
 * <h2>视图集合<h2>
 * 
 * <p>大多数集合都管理着所存储的元素，与之对应的是视图集合，它本身不存储任何元素，
 * 而是依靠支持集合来存储元素。视图集合会将自身未处理的操作委托给支持集合。视图集
 * 合包括：方法返回的包装集合（例如：Collections.checkedCollection、
 * Collections.synchronizedCollection 和 Collections.unmodifiableCollection）、
 * 相同元素的不同表示（例如：List.subList、NavigableSet.subSet 和 Map.entrySet）。
 * 对支持集合的任何修改都会体现在视图集合中，相应地，视图集合上的任何合法操作也
 * 会体现在支持集合中。从技术上来说，视图集合不是真正的集合，但它仍然可以被
 * 迭代器（Iterator 和 ListIterator）修改，并且这种修改也会体现在支持集合中。在
 * 某些情况下，迭代时对支持集合的修改可以被迭代器感知到。
 *
 * <h2>不可修改集合</h2>
 * 
 * <p>该接口中某些方法具有“破坏性”，因为它们操作并修改了集合中的一组对象，这些方
 * 法被称为：修改器。如果实现类不支持这些操作，那么可以抛出
 * UnsupportedOperationException 异常。如果调用这些方法对集合没有产生任何影响，
 * 那么应该（但不强制）抛出 UnsupportedOperationException 异常。试想一下，一个
 * 不支持添加操作的集合调用了 addAll() 方法会发生什么？没有添加任何元素，因此该
 * 集合可以简单地什么也不做，也不抛出异常。但是建议在这种情况下无条件地抛出异常，
 * 而且异常也只会在某些情况下才会造成程序错误。
 *
 * <p>不可修改集合的所有修改器（如上所述）都会抛出 UnsupportedOperationException
 * 异常，因此即使调用修改器也不会改变集合。为了确保集合不可修改，任何从不可修改
 * 集合衍生出的集合也一定是不可修改的，例如：一个不可修改的 List 对象，调用 
 * List.subList 方法得到的集合也是不可修改的。
 *
 * <p>不可修改集合不一定是不可变的。如果集合中的元素可以被修改，那整个集合显然是
 * 可变的，即使它是一个不可修改集合。试想一下，有两个不可修改但是元素可修改的 List
 * 集合，多次调用 list1.equals(list2) 方法可能会产生不同的结果，即使这两个集合是
 * 不可修改的。只有不可修改集合中的元素也是不可修改的，才可以等效的将该集合当成
 * 不可变的。
 *
 * <h2>不可修改的视图集合</h2>
 * 
 * <p>顾名思义，不可修改的视图集合是不可修改的，同样也是支持集合的一个视图。如上
 * 所述，它的修改器会抛出 UnsupportedOperationException 异常，读取和查询的操作会
 * 被委托给支持集合，效果等同于为支持集合设置了只读权限。这对于只需要为用户提供
 * 只读权限的内部集合组件非常有用，同时还能避免他们意外地修改数据。不可修改的
 * 视图集合包括：Collections.unmodifiableCollection、Collections.unmodifiableList
 * 和相关方法。
 *
 * <p>注意：对支持集合的修改可以体现在不可修改的视图集合中，因此，不可修改的视图
 * 集合不一定是不可变的。但是如果支持集合是不可变的，or if the only reference to 
 * the backing collection is through anunmodifiable view, 那么该视图集合可以被
 * 认为是不可变的。
 *
 * <h2>集合序列化</h2>
 * 
 * <p>集合序列化操作是“可选的”，因此所有的集合都没有声明实现 Serializable 接口。
 * 但是序列化通常是有用的，所以大多数实现类都实现了该接口。
 *
 * <p>The collection implementations that are public classes (such as {@code ArrayList}
 * or {@code HashMap}) are declared to implement the {@code Serializable} interface if they
 * are in fact serializable. Some collections implementations are not public classes,
 * such as the <a href="#unmodifiable">unmodifiable collections.</a> In such cases, the
 * serializability of such collections is described in the specification of the method
 * that creates them, or in some other suitable place. In cases where the serializability
 * of a collection is not specified, there is no guarantee about the serializability of such
 * collections. In particular, many <a href="#view">view collections</a> are not serializable.
 *
 * <p>实现 Serializable 接口的集合实现类不保证一定能序列化，因为集合中的元素的类型
 * 是不确定的，
 * <p>A collection implementation that implements the {@code Serializable} interface cannot
 * be guaranteed to be serializable. The reason is that in general, collections
 * contain elements of other types, and it is not possible to determine statically
 * whether instances of some element type are actually serializable. For example, consider
 * a serializable {@code Collection<E>}, where {@code E} does not implement the
 * {@code Serializable} interface. The collection may be serializable, if it contains only
 * elements of some serializable subtype of {@code E}, or if it is empty. Collections are
 * thus said to be <i>conditionally serializable,</i> as the serializability of the collection
 * as a whole depends on whether the collection itself is serializable and on whether all
 * contained elements are also serializable.
 *
 * 
 * <p>An additional case occurs with instances of {@link SortedSet} and {@link SortedMap}.
 * These collections can be created with a {@link Comparator} that imposes an ordering on
 * the set elements or map keys. Such a collection is serializable only if the provided
 * {@code Comparator} is also serializable.
 *
 * <p>该接口是集合框架的组成之一。
 *
 * @implSpec
 * 默认的实现（继承或其它）没有使用任何同步策略，如果实现类需要，可以重写这些方
 * 法以提供同步策略。
 *
 * @param <E> 集合中元素的类型
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     Set
 * @see     List
 * @see     Map
 * @see     SortedSet
 * @see     SortedMap
 * @see     HashSet
 * @see     TreeSet
 * @see     ArrayList
 * @see     LinkedList
 * @see     Vector
 * @see     Collections
 * @see     Arrays
 * @see     AbstractCollection
 * @since 1.2
 *
 * @translator linliangjun
 * @date 2020.11
 */

public interface Collection<E> extends Iterable<E> {
    // 查询操作

    /**
     * 返回集合中元素的个数，最大为 Integer.MAX_VALUE，即使该集合元素的个数超过
     * Integer.MAX_VALUE
     * 
     * @return 集合中元素的个数
     */
    int size();

    /**
     * 如果该集合没有元素，返回 true
     *
     * @return 如果该集合没有元素，返回 true
     */
    boolean isEmpty();

    /**
     * 当集合中包含指定的元素时，返回 ture。更准确地说，当且仅当集合中至少存在一
     * 个元素 e，使得 Objects.equals(o, e) 的值为 ture，则返回值为 true。
     *
     * @param o element whose presence in this collection is to be tested
     * @return {@code true} if this collection contains the specified
     *         element
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this collection
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    boolean contains(Object o);

    /**
     * 返回集合元素的迭代器。该方法不保证迭代的元素顺序（除非某个实现类提供该保
     * 证）
     *
     * @return 集合元素的迭代器
     */
    Iterator<E> iterator();

    /**
     * 
     * Returns an array containing all of the elements in this collection.
     * If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order. The returned array's {@linkplain Class#getComponentType
     * runtime component type} is {@code Object}.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this collection.  (In other words, this method must
     * allocate a new array even if this collection is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * @apiNote
     * This method acts as a bridge between array-based and collection-based APIs.
     * It returns an array whose runtime type is {@code Object[]}.
     * Use {@link #toArray(Object[]) toArray(T[])} to reuse an existing
     * array, or use {@link #toArray(IntFunction)} to control the runtime type
     * of the array.
     *
     * @return an array, whose {@linkplain Class#getComponentType runtime component
     * type} is {@code Object}, containing all of the elements in this collection
     */
    Object[] toArray();

    /**
     * Returns an array containing all of the elements in this collection;
     * the runtime type of the returned array is that of the specified array.
     * If the collection fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this collection.
     *
     * <p>If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element
     * in the array immediately following the end of the collection is set to
     * {@code null}.  (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this collection does
     * not contain any {@code null} elements.)
     *
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * @apiNote
     * This method acts as a bridge between array-based and collection-based APIs.
     * It allows an existing array to be reused under certain circumstances.
     * Use {@link #toArray()} to create an array whose runtime type is {@code Object[]},
     * or use {@link #toArray(IntFunction)} to control the runtime type of
     * the array.
     *
     * <p>Suppose {@code x} is a collection known to contain only strings.
     * The following code can be used to dump the collection into a previously
     * allocated {@code String} array:
     *
     * <pre>
     *     String[] y = new String[SIZE];
     *     ...
     *     y = x.toArray(y);</pre>
     *
     * <p>The return value is reassigned to the variable {@code y}, because a
     * new array will be allocated and returned if the collection {@code x} has
     * too many elements to fit into the existing array {@code y}.
     *
     * <p>Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param <T> the component type of the array to contain the collection
     * @param a the array into which the elements of this collection are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException if the runtime type of any element in this
     *         collection is not assignable to the {@linkplain Class#getComponentType
     *         runtime component type} of the specified array
     * @throws NullPointerException if the specified array is null
     */
    <T> T[] toArray(T[] a);

    /**
     * Returns an array containing all of the elements in this collection,
     * using the provided {@code generator} function to allocate the returned array.
     *
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * @apiNote
     * This method acts as a bridge between array-based and collection-based APIs.
     * It allows creation of an array of a particular runtime type. Use
     * {@link #toArray()} to create an array whose runtime type is {@code Object[]},
     * or use {@link #toArray(Object[]) toArray(T[])} to reuse an existing array.
     *
     * <p>Suppose {@code x} is a collection known to contain only strings.
     * The following code can be used to dump the collection into a newly
     * allocated array of {@code String}:
     *
     * <pre>
     *     String[] y = x.toArray(String[]::new);</pre>
     *
     * @implSpec
     * The default implementation calls the generator function with zero
     * and then passes the resulting array to {@link #toArray(Object[]) toArray(T[])}.
     *
     * @param <T> the component type of the array to contain the collection
     * @param generator a function which produces a new array of the desired
     *                  type and the provided length
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException if the runtime type of any element in this
     *         collection is not assignable to the {@linkplain Class#getComponentType
     *         runtime component type} of the generated array
     * @throws NullPointerException if the generator function is null
     * @since 11
     */
    default <T> T[] toArray(IntFunction<T[]> generator) {
        return toArray(generator.apply(0));
    }

    // Modification Operations

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns {@code true} if this collection changed as a
     * result of the call.  (Returns {@code false} if this collection does
     * not permit duplicates and already contains the specified element.)<p>
     *
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection.  In particular, some
     * collections will refuse to add {@code null} elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     *
     * If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <i>must</i> throw
     * an exception (rather than returning {@code false}).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     *
     * @param e element whose presence in this collection is to be ensured
     * @return {@code true} if this collection changed as a result of the
     *         call
     * @throws UnsupportedOperationException if the {@code add} operation
     *         is not supported by this collection
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this collection
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements
     * @throws IllegalArgumentException if some property of the element
     *         prevents it from being added to this collection
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to insertion restrictions
     */
    boolean add(E e);

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).  More formally,
     * removes an element {@code e} such that
     * {@code Objects.equals(o, e)}, if
     * this collection contains one or more such elements.  Returns
     * {@code true} if this collection contained the specified element (or
     * equivalently, if this collection changed as a result of the call).
     *
     * @param o element to be removed from this collection, if present
     * @return {@code true} if an element was removed as a result of this call
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this collection
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws UnsupportedOperationException if the {@code remove} operation
     *         is not supported by this collection
     */
    boolean remove(Object o);


    // Bulk Operations

    /**
     * Returns {@code true} if this collection contains all of the elements
     * in the specified collection.
     *
     * @param  c collection to be checked for containment in this collection
     * @return {@code true} if this collection contains all of the elements
     *         in the specified collection
     * @throws ClassCastException if the types of one or more elements
     *         in the specified collection are incompatible with this
     *         collection
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this collection does not permit null
     *         elements
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null.
     * @see    #contains(Object)
     */
    boolean containsAll(Collection<?> c);

    /**
     * Adds all of the elements in the specified collection to this collection
     * (optional operation).  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this collection, and this collection is
     * nonempty.)
     *
     * @param c collection containing elements to be added to this collection
     * @return {@code true} if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the {@code addAll} operation
     *         is not supported by this collection
     * @throws ClassCastException if the class of an element of the specified
     *         collection prevents it from being added to this collection
     * @throws NullPointerException if the specified collection contains a
     *         null element and this collection does not permit null elements,
     *         or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this
     *         collection
     * @throws IllegalStateException if not all the elements can be added at
     *         this time due to insertion restrictions
     * @see #add(Object)
     */
    boolean addAll(Collection<? extends E> c);

    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection (optional operation).  After this call returns,
     * this collection will contain no elements in common with the specified
     * collection.
     *
     * @param c collection containing elements to be removed from this collection
     * @return {@code true} if this collection changed as a result of the
     *         call
     * @throws UnsupportedOperationException if the {@code removeAll} method
     *         is not supported by this collection
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not support
     *         null elements
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    boolean removeAll(Collection<?> c);

    /**
     * Removes all of the elements of this collection that satisfy the given
     * predicate.  Errors or runtime exceptions thrown during iteration or by
     * the predicate are relayed to the caller.
     *
     * @implSpec
     * The default implementation traverses all elements of the collection using
     * its {@link #iterator}.  Each matching element is removed using
     * {@link Iterator#remove()}.  If the collection's iterator does not
     * support removal then an {@code UnsupportedOperationException} will be
     * thrown on the first matching element.
     *
     * @param filter a predicate which returns {@code true} for elements to be
     *        removed
     * @return {@code true} if any elements were removed
     * @throws NullPointerException if the specified filter is null
     * @throws UnsupportedOperationException if elements cannot be removed
     *         from this collection.  Implementations may throw this exception if a
     *         matching element cannot be removed or if, in general, removal is not
     *         supported.
     * @since 1.8
     */
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection.
     *
     * @param c collection containing elements to be retained in this collection
     * @return {@code true} if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the {@code retainAll} operation
     *         is not supported by this collection
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not permit null
     *         elements
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    boolean retainAll(Collection<?> c);

    /**
     * Removes all of the elements from this collection (optional operation).
     * The collection will be empty after this method returns.
     *
     * @throws UnsupportedOperationException if the {@code clear} operation
     *         is not supported by this collection
     */
    void clear();


    // Comparison and hashing

    /**
     * Compares the specified object with this collection for equality. <p>
     *
     * While the {@code Collection} interface adds no stipulations to the
     * general contract for the {@code Object.equals}, programmers who
     * implement the {@code Collection} interface "directly" (in other words,
     * create a class that is a {@code Collection} but is not a {@code Set}
     * or a {@code List}) must exercise care if they choose to override the
     * {@code Object.equals}.  It is not necessary to do so, and the simplest
     * course of action is to rely on {@code Object}'s implementation, but
     * the implementor may wish to implement a "value comparison" in place of
     * the default "reference comparison."  (The {@code List} and
     * {@code Set} interfaces mandate such value comparisons.)<p>
     *
     * The general contract for the {@code Object.equals} method states that
     * equals must be symmetric (in other words, {@code a.equals(b)} if and
     * only if {@code b.equals(a)}).  The contracts for {@code List.equals}
     * and {@code Set.equals} state that lists are only equal to other lists,
     * and sets to other sets.  Thus, a custom {@code equals} method for a
     * collection class that implements neither the {@code List} nor
     * {@code Set} interface must return {@code false} when this collection
     * is compared to any list or set.  (By the same logic, it is not possible
     * to write a class that correctly implements both the {@code Set} and
     * {@code List} interfaces.)
     *
     * @param o object to be compared for equality with this collection
     * @return {@code true} if the specified object is equal to this
     * collection
     *
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     * @see List#equals(Object)
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this collection.  While the
     * {@code Collection} interface adds no stipulations to the general
     * contract for the {@code Object.hashCode} method, programmers should
     * take note that any class that overrides the {@code Object.equals}
     * method must also override the {@code Object.hashCode} method in order
     * to satisfy the general contract for the {@code Object.hashCode} method.
     * In particular, {@code c1.equals(c2)} implies that
     * {@code c1.hashCode()==c2.hashCode()}.
     *
     * @return the hash code value for this collection
     *
     * @see Object#hashCode()
     * @see Object#equals(Object)
     */
    int hashCode();

    /**
     * Creates a {@link Spliterator} over the elements in this collection.
     *
     * Implementations should document characteristic values reported by the
     * spliterator.  Such characteristic values are not required to be reported
     * if the spliterator reports {@link Spliterator#SIZED} and this collection
     * contains no elements.
     *
     * <p>The default implementation should be overridden by subclasses that
     * can return a more efficient spliterator.  In order to
     * preserve expected laziness behavior for the {@link #stream()} and
     * {@link #parallelStream()} methods, spliterators should either have the
     * characteristic of {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <em><a href="Spliterator.html#binding">late-binding</a></em>.
     * If none of these is practical, the overriding class should describe the
     * spliterator's documented policy of binding and structural interference,
     * and should override the {@link #stream()} and {@link #parallelStream()}
     * methods to create streams using a {@code Supplier} of the spliterator,
     * as in:
     * <pre>{@code
     *     Stream<E> s = StreamSupport.stream(() -> spliterator(), spliteratorCharacteristics)
     * }</pre>
     * <p>These requirements ensure that streams produced by the
     * {@link #stream()} and {@link #parallelStream()} methods will reflect the
     * contents of the collection as of initiation of the terminal stream
     * operation.
     *
     * @implSpec
     * The default implementation creates a
     * <em><a href="Spliterator.html#binding">late-binding</a></em> spliterator
     * from the collection's {@code Iterator}.  The spliterator inherits the
     * <em>fail-fast</em> properties of the collection's iterator.
     * <p>
     * The created {@code Spliterator} reports {@link Spliterator#SIZED}.
     *
     * @implNote
     * The created {@code Spliterator} additionally reports
     * {@link Spliterator#SUBSIZED}.
     *
     * <p>If a spliterator covers no elements then the reporting of additional
     * characteristic values, beyond that of {@code SIZED} and {@code SUBSIZED},
     * does not aid clients to control, specialize or simplify computation.
     * However, this does enable shared use of an immutable and empty
     * spliterator instance (see {@link Spliterators#emptySpliterator()}) for
     * empty collections, and enables clients to determine if such a spliterator
     * covers no elements.
     *
     * @return a {@code Spliterator} over the elements in this collection
     * @since 1.8
     */
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, 0);
    }

    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     *
     * <p>This method should be overridden when the {@link #spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
     * for details.)
     *
     * @implSpec
     * The default implementation creates a sequential {@code Stream} from the
     * collection's {@code Spliterator}.
     *
     * @return a sequential {@code Stream} over the elements in this collection
     * @since 1.8
     */
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns a possibly parallel {@code Stream} with this collection as its
     * source.  It is allowable for this method to return a sequential stream.
     *
     * <p>This method should be overridden when the {@link #spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
     * for details.)
     *
     * @implSpec
     * The default implementation creates a parallel {@code Stream} from the
     * collection's {@code Spliterator}.
     *
     * @return a possibly parallel {@code Stream} over the elements in this
     * collection
     * @since 1.8
     */
    default Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}