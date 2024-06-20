package bangkit.capstone.waterwise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    version = 1,
    exportSchema = false
)
abstract class PostDatabase : RoomDatabase() {
//
//    abstract fun storyDao(): StoryDao
//    abstract fun remoteKeyDao(): RemoteKeyDao
//    companion object {
//        @Volatile
//        private var INSTANCE: PostDatabase? = null
//
//        @JvmStatic
//        fun getDatabase(context: Context): PostDatabase {
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    PostDatabase::class.java, "story_database"
//                )
//                    .fallbackToDestructiveMigration()
//                    .build()
//                    .also { INSTANCE = it }
//            }
//        }
//    }
}