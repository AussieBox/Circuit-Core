## Changes
- Make ItemGroups registered through an entrypoint
  - To register an ItemGroup, you should now create a class that implements ```ItemGroupSupplier```.
  - Then,  reference that class in the ```"circ_item_groups"``` custom entrypoint.
- Many internals for Item and Block handling have been changed
  - To register items, call ```ItemRegistry.register()```, and use ```ItemBuilder``` instances.
    - You can retrieve the built item with ```ItemBuilder.build()```. Calling it multiple times is a-okay!
  - To register blocks, continue using ```BlockHelper```.
    - However, register ```BlockItem```s with the ```ItemRegistry```, albeit with ```BlockItemBuilder``` instances.