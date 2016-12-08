/*
3. Look at the 'BitSet' class,
and make a diagram of all its superclasses and traits.
Ignore the type parameters (everything inside the [...]).
Then give the linearization of the traits.

The linearization is a technical specification of all supertypes of a type.
The linearization gives the order in which super is resolved in a trait

the rule: If
    C extends C1 with C2 with . . . with Cn,
then
    lin(C) = C » lin(Cn) » . . . » lin(C2) » lin(C1)
Here, » means “concatenate and remove duplicates, with the right winning out.”

trait BitSet
    extends SortedSet[Int]
    with BitSetLike[BitSet]

lin(BitSet) = BitSet
    >> lin(BitSetLike)
    >> lin(SortedSet)

###############################################################################
It was a hell of a job, very tedious.
Don't do it if you don't interested in stdlib internals

lin(BitSet) = BitSet
    >> BitSetLike
    >> SortedSet
    >> SortedSetLike
    >> Sorted
    >> Set
    >> SetLike
    >> Subtractable
    >> GenSet
    >> GenericSetTemplate
    >> GenSetLike
    >> Iterable
    >> IterableLike
    >> Equals
    >> GenIterable
    >> GenIterableLike
    >> Traversable
    >> GenTraversable
    >> GenericTraversableTemplate
    >> TraversableLike
    >> GenTraversableLike
    >> Parallelizable
    >> TraversableOnce
    >> GenTraversableOnce
    >> FilterMonadic
    >> HasNewBuilder >> Any
    >> A => Boolean

###############################################################################

lin(BitSetLike) = BitSetLike
    >> lin(SortedSetLike)
    >> lin(SortedSet)

lin(BitSetLike) = BitSetLike
    >> SortedSetLike
    >> SetLike
    >> Subtractable
    >> GenSetLike
        >> A => Boolean
    >> IterableLike
    >> GenIterableLike
    >> TraversableLike
        >> GenTraversableLike >> Parallelizable
        >> TraversableOnce >> GenTraversableOnce
        >> FilterMonadic
        >> HasNewBuilder
    >> Equals
    >> Any
    >> Sorted
    >> SortedSet
    >> SortedSetLike
    >> SetLike
    >> Subtractable
    >> GenSetLike
        >> A => Boolean
    >> IterableLike
    >> GenIterableLike
    >> TraversableLike
        >> GenTraversableLike >> Parallelizable
        >> TraversableOnce >> GenTraversableOnce
        >> FilterMonadic
        >> HasNewBuilder
    >> Equals
    >> Any
    >> Sorted
    >> Set
    >> SetLike
        >> Parallelizable >> Any
        >> Subtractable
        >> GenSetLike
            >> Equals
            >> A => Boolean
            >> GenIterableLike
            >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
            >> Any
        >> IterableLike
        >> GenIterableLike
        >> TraversableLike
            >> GenTraversableLike >> Parallelizable
            >> TraversableOnce >> GenTraversableOnce
            >> FilterMonadic
            >> HasNewBuilder
        >> Equals
        >> Any
    >> GenericSetTemplate >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenSet
        >> GenericSetTemplate >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenIterable
            >> GenTraversable
            >> GenericTraversableTemplate >> HasNewBuilder
            >> GenIterableLike
                >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
                >> Any
        >> GenSetLike
        >> Equals
        >> A => Boolean
        >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
        >> Any
    >> Iterable
    >> IterableLike
        >> GenIterableLike
            >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> TraversableLike
            >> GenTraversableLike >> Parallelizable
            >> TraversableOnce >> GenTraversableOnce
            >> FilterMonadic
            >> HasNewBuilder >> Any
        >> Equals
        >> Any
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenIterable
        >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenTraversable
        >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenTraversableOnce >> Any
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> Any
    >> Traversable
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> TraversableOnce >> GenTraversableOnce >> Any
    >> GenTraversable >> GenericTraversableTemplate >> HasNewBuilder >> GenTraversableLike
        >> Parallelizable >> GenTraversableOnce >> Any
    >> TraversableLike
        >> Parallelizable >> Any
        >> GenTraversableLike >> Parallelizable >> Any >> GenTraversableOnce >> Any
        >> TraversableOnce >> GenTraversableOnce >> Any
        >> FilterMonadic >> Any
        >> HasNewBuilder >> Any
    >> A => Boolean

lin(SortedSet) = SortedSet
    >> SortedSetLike
        >> SetLike
        >> Subtractable
        >> GenSetLike
            >> A => Boolean
        >> IterableLike
        >> GenIterableLike
        >> TraversableLike
            >> GenTraversableLike >> Parallelizable
            >> TraversableOnce >> GenTraversableOnce
            >> FilterMonadic
            >> HasNewBuilder
        >> Equals
        >> Any
        >> Sorted
    >> Set
    >> SetLike
        >> Parallelizable >> Any
        >> Subtractable
        >> GenSetLike
            >> Equals
            >> A => Boolean
            >> GenIterableLike
            >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
            >> Any
        >> IterableLike
        >> GenIterableLike
        >> TraversableLike
            >> GenTraversableLike >> Parallelizable
            >> TraversableOnce >> GenTraversableOnce
            >> FilterMonadic
            >> HasNewBuilder
        >> Equals
        >> Any
    >> GenericSetTemplate >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenSet
        >> GenericSetTemplate >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenIterable
            >> GenTraversable
            >> GenericTraversableTemplate >> HasNewBuilder
            >> GenIterableLike
                >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
                >> Any
        >> GenSetLike
        >> Equals
        >> A => Boolean
        >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
        >> Any
    >> Iterable
    >> IterableLike
        >> GenIterableLike
            >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> TraversableLike
            >> GenTraversableLike >> Parallelizable
            >> TraversableOnce >> GenTraversableOnce
            >> FilterMonadic
            >> HasNewBuilder >> Any
        >> Equals
        >> Any
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenIterable
        >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenTraversable
        >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenTraversableOnce >> Any
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> Any
    >> Traversable
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> TraversableOnce >> GenTraversableOnce >> Any
    >> GenTraversable >> GenericTraversableTemplate >> HasNewBuilder >> GenTraversableLike
        >> Parallelizable >> GenTraversableOnce >> Any
    >> TraversableLike
        >> Parallelizable >> Any
        >> GenTraversableLike >> Parallelizable >> Any >> GenTraversableOnce >> Any
        >> TraversableOnce >> GenTraversableOnce >> Any
        >> FilterMonadic >> Any
        >> HasNewBuilder >> Any
    >> A => Boolean

lin(SortedSetLike) = SortedSetLike
    >> SetLike
    >> Subtractable
    >> GenSetLike
        >> A => Boolean
    >> IterableLike
    >> GenIterableLike
    >> TraversableLike
        >> GenTraversableLike >> Parallelizable
        >> TraversableOnce >> GenTraversableOnce
        >> FilterMonadic
        >> HasNewBuilder
    >> Equals
    >> Any
    >> Sorted

lin(Set) = Set
    >> lin(SetLike)
    >> lin(GenericSetTemplate)
    >> lin(GenSet)
    >> lin(Iterable) >> A => Boolean

lin(Set) = Set
    >> SetLike
        >> Parallelizable >> Any
        >> Subtractable
        >> GenSetLike
            >> Equals
            >> A => Boolean
            >> GenIterableLike
            >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
            >> Any
        >> IterableLike
        >> GenIterableLike
        >> TraversableLike
            >> GenTraversableLike >> Parallelizable
            >> TraversableOnce >> GenTraversableOnce
            >> FilterMonadic
            >> HasNewBuilder
        >> Equals
        >> Any
    >> GenericSetTemplate >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenSet
        >> GenericSetTemplate >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenIterable
            >> GenTraversable
            >> GenericTraversableTemplate >> HasNewBuilder
            >> GenIterableLike
                >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
                >> Any
        >> GenSetLike
        >> Equals
        >> A => Boolean
        >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
        >> Any
    >> Iterable
    >> IterableLike
        >> GenIterableLike
            >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> TraversableLike
            >> GenTraversableLike >> Parallelizable
            >> TraversableOnce >> GenTraversableOnce
            >> FilterMonadic
            >> HasNewBuilder >> Any
        >> Equals
        >> Any
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenIterable
        >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenTraversable
        >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenTraversableOnce >> Any
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> Any
    >> Traversable
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> TraversableOnce >> GenTraversableOnce >> Any
    >> GenTraversable >> GenericTraversableTemplate >> HasNewBuilder >> GenTraversableLike
        >> Parallelizable >> GenTraversableOnce >> Any
    >> TraversableLike
        >> Parallelizable >> Any
        >> GenTraversableLike >> Parallelizable >> Any >> GenTraversableOnce >> Any
        >> TraversableOnce >> GenTraversableOnce >> Any
        >> FilterMonadic >> Any
        >> HasNewBuilder >> Any
    >> A => Boolean

lin(SetLike) = SetLike
    >> Parallelizable >> Any
    >> Subtractable
    >> GenSetLike
        >> Equals
        >> A => Boolean
        >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
        >> Any
    >> IterableLike
    >> GenIterableLike
    >> TraversableLike
        >> GenTraversableLike >> Parallelizable
        >> TraversableOnce >> GenTraversableOnce
        >> FilterMonadic
        >> HasNewBuilder
    >> Equals
    >> Any

lin(GenSet) = GenSet
    >> GenericSetTemplate >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenIterable
        >> GenTraversable
        >> GenericTraversableTemplate >> HasNewBuilder
        >> GenIterableLike
            >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
            >> Any
    >> GenSetLike
    >> Equals
    >> A => Boolean
    >> GenIterableLike
    >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
    >> Any

lin(GenSetLike) = GenSetLike
    >> Equals
    >> A => Boolean
    >> GenIterableLike
    >> GenTraversableLike >> Parallelizable >> GenTraversableOnce
    >> Any

lin(GenericSetTemplate) = GenericSetTemplate >> GenericTraversableTemplate >> HasNewBuilder >> Any

lin(Iterable) = Iterable
    >> lin(IterableLike)
    >> lin(GenericTraversableTemplate)
    >> lin(GenIterable)
    >> lin(Traversable)

lin(Iterable) = Iterable
    >> IterableLike
        >> GenIterableLike
            >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> TraversableLike
            >> GenTraversableLike >> Parallelizable
            >> TraversableOnce >> GenTraversableOnce
            >> FilterMonadic
            >> HasNewBuilder >> Any
        >> Equals
        >> Any
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenIterable
        >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenTraversable
        >> GenericTraversableTemplate >> HasNewBuilder >> Any
        >> GenTraversableOnce >> Any
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> Any
    >> Traversable
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> TraversableOnce >> GenTraversableOnce >> Any
    >> GenTraversable >> GenericTraversableTemplate >> HasNewBuilder >> GenTraversableLike
        >> Parallelizable >> GenTraversableOnce >> Any
    >> TraversableLike
        >> Parallelizable >> Any
        >> GenTraversableLike >> Parallelizable >> Any >> GenTraversableOnce >> Any
        >> TraversableOnce >> GenTraversableOnce >> Any
        >> FilterMonadic >> Any
        >> HasNewBuilder >> Any

lin(IterableLike) = IterableLike
    >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
    >> TraversableLike
        >> GenTraversableLike >> Parallelizable
        >> TraversableOnce >> GenTraversableOnce
        >> FilterMonadic
        >> HasNewBuilder >> Any
    >> Equals
    >> Any

lin(GenIterable) = GenIterable
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenTraversable
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenTraversableOnce >> Any
    >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
    >> GenIterableLike
        >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
        >> Any

lin(GenIterableLike) = GenIterableLike
    >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any
    >> Any

lin(GenTraversableLike) = GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any

lin(GenTraversable) = GenTraversable
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenTraversableOnce >> Any
    >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any

lin(GenericTraversableTemplate) = GenericTraversableTemplate >> HasNewBuilder >> Any

lin(Traversable) = Traversable
    >> lin(GenericTraversableTemplate)
    >> lin(TraversableOnce)
    >> lin(GenTraversable)
    >> lin(TraversableLike)

lin(Traversable) = Traversable
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> TraversableOnce >> GenTraversableOnce >> Any
    >> GenTraversable >> GenericTraversableTemplate >> HasNewBuilder >> GenTraversableLike
        >> Parallelizable >> GenTraversableOnce >> Any
    >> TraversableLike
        >> Parallelizable >> Any
        >> GenTraversableLike >> Parallelizable >> Any >> GenTraversableOnce >> Any
        >> TraversableOnce >> GenTraversableOnce >> Any
        >> FilterMonadic >> Any
        >> HasNewBuilder >> Any

lin(TraversableOnce) = TraversableOnce >> GenTraversableOnce >> Any

lin(GenTraversable) = GenTraversable
    >> lin(GenericTraversableTemplate)
    >> lin(GenTraversableOnce)
    >> lin(GenTraversableLike)

lin(GenTraversable) = GenTraversable
    >> GenericTraversableTemplate >> HasNewBuilder >> Any
    >> GenTraversableOnce >> Any
    >> GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any

lin(GenTraversableLike) = GenTraversableLike >> Parallelizable >> GenTraversableOnce >> Any

lin(GenTraversableOnce) = GenTraversableOnce >> Any

lin(GenericTraversableTemplate) = GenericTraversableTemplate >> HasNewBuilder >> Any

lin(TraversableLike) = TraversableLike
    >> Parallelizable >> Any
    >> GenTraversableLike >> Parallelizable >> Any >> GenTraversableOnce >> Any
    >> TraversableOnce >> GenTraversableOnce >> Any
    >> FilterMonadic >> Any
    >> HasNewBuilder >> Any

lin(GenTraversableLike) = GenTraversableLike
    >> Parallelizable >> Any
    >> GenTraversableOnce >> Any

lin(Parallelizable) = Parallelizable >> Any

lin(HasNewBuilder) = HasNewBuilder >> Any
lin(FilterMonadic) = FilterMonadic >> Any

lin(TraversableOnce) = TraversableOnce
    >> lin(GenTraversableOnce) >> Any

lin(TraversableOnce) = TraversableOnce
    >> lin(GenTraversableOnce) >> Any

lin(TraversableOnce) = TraversableOnce >> GenTraversableOnce >> Any

lin(GenTraversableOnce) = GenTraversableOnce >> Any


trait SortedSet[A] extends Set[A]
    with SortedSetLike[A, SortedSet[A]]
lin(SortedSet) = SortedSet >> lin(SortedSetLike) >> lin(Set)

trait Set[A] extends (A => Boolean)
    with Iterable[A]
    with GenSet[A]
    with GenericSetTemplate[A, Set]
    with SetLike[A, Set[A]]
lin(Set) = Set >> lin(SetLike) >> lin(GenericSetTemplate) >> lin(GenSet) >> lin(Iterable)
    >> lin(A => Boolean)

lin(A => Boolean) = A => Boolean

trait Iterable[+A] extends Traversable[A]
    with GenIterable[A]
    with GenericTraversableTemplate[A, Iterable]
    with IterableLike[A, Iterable[A]]
lin(Iterable) = Iterable >> lin(IterableLike) >> lin(GenericTraversableTemplate)
    >> lin(GenIterable) >> lin(Traversable)

trait Traversable[+A] extends TraversableLike[A, Traversable[A]]
    with GenTraversable[A]
    with TraversableOnce[A]
    with GenericTraversableTemplate[A, Traversable]
lin(Traversable) = Traversable >> lin(GenericTraversableTemplate) >> lin(TraversableOnce)
    >> lin(GenTraversable) >> lin(TraversableLike)

trait TraversableLike[+A, +Repr] extends Any
    with HasNewBuilder[A, Repr]
    with FilterMonadic[A, Repr]
    with TraversableOnce[A]
    with GenTraversableLike[A, Repr]
    with Parallelizable[A, ParIterable[A]]
lin(TraversableLike) = TraversableLike >> lin(Parallelizable) >> lin(GenTraversableLike)
    >> lin(TraversableOnce) >> lin(FilterMonadic) >> lin(HasNewBuilder) >> lin(Any)

lin(Any) = Any

trait HasNewBuilder[+A, +Repr] extends Any
lin(HasNewBuilder) = HasNewBuilder >> Any

trait FilterMonadic[+A, +Repr] extends Any
lin(FilterMonadic) = FilterMonadic >> Any

trait TraversableOnce[+A] extends Any
    with GenTraversableOnce[A]
lin(TraversableOnce) = TraversableOnce >> lin(GenTraversableOnce) >> Any

trait GenTraversableOnce[+A] extends Any
lin(GenTraversableOnce) = GenTraversableOnce >> Any

trait GenTraversableLike[+A, +Repr] extends Any
    with GenTraversableOnce[A]
    with Parallelizable[A, parallel.ParIterable[A]]
lin(GenTraversableLike) = GenTraversableLike >> lin(Parallelizable) >> GenTraversableOnce >> Any

trait Parallelizable[+A, +ParRepr <: Parallel] extends Any
lin(Parallelizable) = Parallelizable >> Any

trait GenTraversable[+A] extends GenTraversableLike[A, GenTraversable[A]]
    with GenTraversableOnce[A]
    with GenericTraversableTemplate[A, GenTraversable]
lin(GenTraversable) = GenTraversable >> lin(GenericTraversableTemplate) >> lin(GenTraversableOnce)
    >> lin(GenTraversableLike)

trait GenericTraversableTemplate[+A, +CC[X] <: GenTraversable[X]]
    extends HasNewBuilder[A, CC[A] @uncheckedVariance]
lin(GenericTraversableTemplate) = GenericTraversableTemplate >> lin(HasNewBuilder)

trait GenIterable[+A] extends GenIterableLike[A, GenIterable[A]]
    with GenTraversable[A]
    with GenericTraversableTemplate[A, GenIterable]
lin(GenIterable) = GenIterable >> lin(GenericTraversableTemplate) >> lin(GenTraversable)
    >> lin(GenIterableLike)

trait GenIterableLike[+A, +Repr] extends Any
    with GenTraversableLike[A, Repr]
lin(GenIterableLike) = GenIterableLike >> lin(GenTraversableLike) >> Any

trait IterableLike[+A, +Repr] extends Any
    with Equals
    with TraversableLike[A, Repr]
    with GenIterableLike[A, Repr]
lin(IterableLike) = IterableLike >> lin(GenIterableLike) >> lin(TraversableLike)
    >> lin(Equals) >> Any

trait Equals extends Any
lin(Equals) = Equals >> Any

trait GenSet[A] extends GenSetLike[A, GenSet[A]]
    with GenIterable[A]
    with GenericSetTemplate[A, GenSet]
lin(GenSet) = GenSet >> lin(GenericSetTemplate) >> lin(GenIterable) >> lin(GenSetLike)

trait GenSetLike[A, +Repr] extends GenIterableLike[A, Repr]
    with (A => Boolean)
    with Equals
    with Parallelizable[A, parallel.ParSet[A]]
lin(GenSetLike) = GenSetLike >> lin(Parallelizable) >> lin(Equals) >> A => Boolean
    >> lin(GenIterableLike)

trait GenericSetTemplate[A, +CC[X] <: GenSet[X]] extends GenericTraversableTemplate[A, CC]
lin(GenericSetTemplate) = GenericSetTemplate >> lin(GenericTraversableTemplate)

trait SetLike[A, +This <: SetLike[A, This] with Set[A]] extends IterableLike[A, This]
    with GenSetLike[A, This]
    with Subtractable[A, This]
    with Parallelizable[A, ParSet[A]]
lin(SetLike) = SetLike >> lin(Parallelizable) >> lin(Subtractable) >> lin(GenSetLike)
    >> lin(IterableLike)

lin(Subtractable) = Subtractable

trait SortedSetLike[A, +This <: SortedSet[A] with SortedSetLike[A, This]]
    extends Sorted[A, This]
    with SetLike[A, This]
lin(SortedSetLike) = SortedSetLike >> lin(SetLike) >> lin(Sorted)

lin(Sorted) = Sorted

trait BitSetLike[+This <: BitSetLike[This]
    with SortedSet[Int]]
    extends SortedSetLike[Int, This]
lin(BitSetLike) = BitSetLike >> lin(SortedSetLike) >> lin(SortedSet)

 */
