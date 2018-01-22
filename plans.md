# Plans for today

- <input type="checkbox" checked/> Allow for the creation of Entries in the database
   - <input type="checkbox" checked/>
     Make database connection static (prereq.)
   - <input type="checkbox" checked/>
     Make database respect foreign key constraints (prereq.)
   - <input type="checkbox" checked/>
     On update/delete cascade (prereq.)
   - <input type="checkbox" checked/>
     Make content of list visible in ViewListActivity
   - <input type="checkbox" checked/>
     Create an EditDatabaseEntryDialog
      - <input type="checkbox" checked/>
        Create basic structure
      - <input type="checkbox" checked/>
        Handle empty fields
      - <input type="checkbox" checked/>
        Handle input fields must contain correct character types
        - <input type="checkbox" checked/>
          Create class JapaneseTextProcessingUtilities.java
        - <input type="checkbox" checked/>
          Add public static method isKanji(char)
        - <input type="checkbox" checked/>
          Add public static method isHiragana(char)
        - <input type="checkbox" checked/>
          Add public static method isKatakana(char)
        - <input type="checkbox" checked/>
          Add public static method validWordKanji(String)
        - <input type="checkbox" checked/>
          Add public static method validWordReading(String)
      - <input type="checkbox" checked/>
        Write to db on dismiss
   - <input type="checkbox" checked/>
     Put CreateDatabaseEntryDialog in FindWordsActivity
   - <input type="checkbox" checked/>
    Put CreateNewListDialog in FindWordsActivity
- <input type="checkbox" />
  Create editing mechanism of entries in ViewList
  - <input type="checkbox" />
    Create drag-and-drop reordering mechanism
- <input type="checkbox"/>
  Implement tags and tag filtering in ViewList
- <input type="checkbox"/>
  Implement searching (filtering?) in ViewList
- <input type="checkbox"/>
  Add style to all the dialogs I've made thus far
- <input type="checkbox"/>
  Remove magic string literals and put them in string resources
- <input type="checkbox"/>
  Clean up imports
- <input type="checkbox"/>
  Add documentation
