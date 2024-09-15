# Game Action

Interfaces for various actions. Actions can be a functional void (they return nothing) interface like:

* [VoidAction](VoidAction.java) - For no parameter
* [Action](Action.java) - For one parameter
* [BiAction](BiAction.java) - For two parameters

Or they are used as a kind of marker for certain guaranteed functionality like:

* [Hideable](Hideable.java) - For hiding ui elements
* [Valuable](Valuable.java) - For setting or getting values from a component
* [Resettable](Resettable.java) - For resetting the state of a component
* ...