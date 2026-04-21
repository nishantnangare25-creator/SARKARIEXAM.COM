package sarkari.exam_ai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import sarkari.exam_ai.data.local.entities.SavedPdfEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SarkariDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePdf(pdf: SavedPdfEntity)

    @Query("SELECT * FROM saved_pdfs ORDER BY savedAt DESC")
    fun getAllSavedPdfs(): Flow<List<SavedPdfEntity>>

    @Query("DELETE FROM saved_pdfs WHERE id = :pdfId")
    suspend fun deletePdf(pdfId: Int)

    @Query("SELECT EXISTS(SELECT * FROM saved_pdfs WHERE id = :pdfId)")
    suspend fun isPdfSaved(pdfId: Int): Boolean
}
