## Additions
- Added ```ItemSupplier```, which is initialised before ```ItemRegistry``` is baked.
  - To ensure items are registered, put them in a class extending ```ItemSupplier```, and add it to the ```circ-items``` entrypoint.