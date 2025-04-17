package com.example.noted;

public class konfigurasi {

    // PENTING! Ganti IP ini sesuai IP komputer kamu yang menjalankan server PHP
    public static final String URL_BASE = "http://10.8.13.200/Android/noted/";

    // ----------------------
    // NOTE-related Endpoints
    // ----------------------
    public static final String URL_ADD_NOTE = URL_BASE + "addNote.php";
    public static final String URL_GET_ALL_NOTES = URL_BASE + "showNote.php";
    public static final String URL_GET_NOTE_DETAIL = URL_BASE + "showNoteDetail.php?id=";
    public static final String URL_UPDATE_NOTE = URL_BASE + "updateNote.php";
    public static final String URL_DELETE_NOTE = URL_BASE + "deleteNote.php?id=";

    // ------------------------
    // FOLDER-related Endpoints
    // ------------------------
    public static final String URL_ADD_FOLDER = URL_BASE + "addFolder.php";
    public static final String URL_GET_ALL_FOLDERS = URL_BASE + "showFolder.php";
    public static final String URL_GET_FOLDER_DETAIL = URL_BASE + "showFolderDetail.php?id=";
    public static final String URL_UPDATE_FOLDER = URL_BASE + "updateFolder.php";
    public static final String URL_DELETE_FOLDER = URL_BASE + "deleteFolder.php?id=";

    // --------------------
    // NOTE-related Keys
    // --------------------
    public static final String KEY_NOTE_ID = "id";
    public static final String KEY_NOTE_TITLE = "title";
    public static final String KEY_NOTE_CONTENT = "content";
    public static final String KEY_NOTE_FOLDER_ID = "folder_id";


    // --------------------
    // FOLDER-related Keys
    // --------------------
    public static final String KEY_FOLDER_ID = "id";
    public static final String KEY_FOLDER_NAME = "name";


    // --------------------
    // JSON Tags (Note)
    // --------------------
    public static final String TAG_JSON_ARRAY = "result";
    public static final String TAG_ID = "id";
    public static final String TAG_TITLE = "title";
    public static final String TAG_CONTENT = "content";
    public static final String TAG_FOLDER_ID = "folder_id";
    public static final String TAG_CREATED_AT = "created_at";

    // --------------------
    // JSON Tags (Folder)
    // --------------------
    public static final String TAG_FOLDER_NAME = "name";

    // --------------------
    // Extra ID tags
    // --------------------
    public static final String NOTE_ID = "note_id";
    public static final String FOLDER_ID_EXTRA = "folder_id";
}
