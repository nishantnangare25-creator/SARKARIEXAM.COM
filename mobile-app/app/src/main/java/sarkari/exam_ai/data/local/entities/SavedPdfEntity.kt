package sarkari.exam_ai.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_pdfs")
data class SavedPdfEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val size: String,
    val category: String,
    val downloadUrl: String,
    val localFilePath: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)
