package com.example.noted;

public class konfigurasi {

    // PENTING! Ganti IP ini sesuai IP komputer kamu yang menjalankan server PHP
    public static final String URL_BASE = "http://10.2.6.144/Android/noted/";

    // ----------------------
    // USER-related Endpoints
    // ----------------------
    public static final String URL_ADD_USER = URL_BASE + "userHandler.php?action=create";
    public static final String URL_LOGIN = URL_BASE + "userHandler.php?action=login";

    public static final String URL_CHANGE_PASSWORD = URL_BASE + "changePassword.php";

    // ----------------------
    // NOTE-related Endpoints
    // ----------------------
    public static final String URL_ADD_NOTE = URL_BASE + "addNote.php";
    public static final String URL_GET_ALL_NOTES = URL_BASE + "showNote.php";
    public static final String URL_GET_NOTE_DETAIL = URL_BASE + "showNoteDetail.php?";
    public static final String URL_UPDATE_NOTE = URL_BASE + "updateNote.php";
    public static final String URL_DELETE_NOTE = URL_BASE + "deleteNote.php?id=";

    // ------------------------
    // FOLDER-related Endpoints
    // ------------------------
    public static final String URL_ADD_FOLDER = URL_BASE + "addFolder.php";
    public static final String URL_GET_ALL_FOLDERS = URL_BASE + "showFolder.php";
    public static final String URL_GET_FOLDER_DETAIL = URL_BASE + "showFolderDetail.php?";
    public static final String URL_UPDATE_FOLDER = URL_BASE + "updateFolder.php";
    public static final String URL_DELETE_FOLDER = URL_BASE + "deleteFolder.php?folder_id=";

    // --------------------
    // USER-related Keys (Field pada tb_users)
    // --------------------
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_PASSWORD = "password";

    // --------------------
    // NOTE-related Keys (Field pada tb_notes)
    // --------------------
    public static final String KEY_NOTE_ID = "id";
    public static final String KEY_NOTE_TITLE = "title";
    public static final String KEY_NOTE_CONTENT = "content";
    public static final String KEY_NOTE_FOLDER_ID = "folder_id";   // Tambahkan ini supaya bisa simpan folder_id
    public static final String KEY_NOTE_CREATED_AT = "created_at"; // Tambahkan ini untuk created_at

    // --------------------
    // FOLDER-related Keys (Field pada tb_folder)
    // --------------------
    public static final String KEY_FOLDER_ID = "folder_id";
    public static final String KEY_FOLDER_NAME = "name";

    // --------------------
    // JSON Tags (untuk parsing JSON dari server)
    // --------------------
    public static final String TAG_JSON_ARRAY = "result";

    // User Tags
    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_USER_NAME = "name";
    public static final String TAG_USER_EMAIL = "email";

    // Note Tags
    public static final String TAG_ID = "id";
    public static final String TAG_TITLE = "title";
    public static final String TAG_CONTENT = "content";
    public static final String TAG_FOLDER_ID = "folder_id";  // Tambahkan TAG_FOLDER_ID di Note juga
    public static final String TAG_CREATED_AT = "created_at";

    // Folder Tags
    public static final String TAG_FOLDER_NAME = "name";

    // --------------------
    // Extra ID Tags
    // --------------------
    public static final String NOTE_ID = "note_id";
    public static final String FOLDER_ID = "folder_id"; // Ini opsional, tergantung penggunaan nanti
}