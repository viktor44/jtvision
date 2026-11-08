# JT Vision website

Docusaurus site. The API reference in `docs/api` is generated from the javadoc comments by
[javadoc2md](../../javadoc2md) and is committed, so it is **not** regenerated on `npm start`/`npm run build`.

```
npm install
npm start        # dev server on http://localhost:3000/jtvision/
npm run build    # static site in build/
```

## Regenerating API pages

Needs JDK 17+ and `javadoc2md` installed in the local Maven repo (`mvn install` in `javadoc2md`).
From the project root:

```
mvn javadoc:javadoc -Pmarkdown-docs
```

Output goes to `website/docs/api`. The profile delomboks the sources first so Lombok-generated members are documented.
