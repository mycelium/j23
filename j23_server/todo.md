

### Refactoring

~~0. Commit current state~~
1. Segregate layers: API, BL, DAO (use SOLID!!!) (Major)
- Remove dependency on User (dto) from BL and DAO
- Remove dependency on DAO from BL
- Create API layer (JSON, Java, etc)
- Remove dependency on DAO from API
2. Replace `System.out` with Logger (Minor)
- No `System.out` in project


### Issues (TODO):

1. Accept JSONs as input (Should I support JavaAPI?) (depends on refactoring 1)
2. Switch to another DB (What DB?) (depends on refactoring 1)