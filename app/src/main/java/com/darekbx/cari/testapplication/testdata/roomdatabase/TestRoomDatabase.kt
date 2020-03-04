package com.darekbx.cari.testapplication.testdata.roomdatabase

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TestRoomDatabase {

    @Entity(tableName = "person")
    class PersonDto(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "age") var age: Int = 0,
        @ColumnInfo(name = "active") var active: Boolean = false,
        @ColumnInfo(name = "company_id") var companyId: Long? = null
    )

    @Entity(tableName = "company")
    class CompanyDto(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "address") var address: String = ""
    )

    @Database(entities = arrayOf(PersonDto::class, CompanyDto::class), version = 3)
    abstract class TestDatabase : RoomDatabase() {

        companion object {
            val DB_NAME = "room_db"
        }

        abstract fun copanyDao(): CompanyDao

        abstract fun personDao(): PersonDao
    }

    @Dao
    interface CompanyDao {
        @Insert
        fun add(companyDto: CompanyDto): Long
    }

    @Dao
    interface PersonDao {
        @Insert
        fun add(personDto: PersonDto): Long
    }

    fun createTestDatabase(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = Room.databaseBuilder(context, TestDatabase::class.java, TestDatabase.DB_NAME)
                .addMigrations(object: Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) { }
                })
                .addMigrations(object: Migration(2, 3) {
                    override fun migrate(database: SupportSQLiteDatabase) { }
                })
                .build()
            with (database){
                clearAllTables()

                val companyDao = copanyDao()
                val companyOneId = companyDao.add(CompanyDto(name = "Company One", address = "Milky Road 1, 02412 NY"))
                val companyTwoId = companyDao.add(CompanyDto(name = "Company Two", address = "Star Road 42, 46321 LA"))

                personDao().run {
                    add(PersonDto(name = "John Smith", age = 35, active = true, companyId = companyOneId))
                    add(PersonDto(name = "Daniel Brown", age = 56, active = false, companyId = companyOneId))
                    add(PersonDto(name = "Mike Miller", age = 19, active = true, companyId = companyTwoId))
                    add(PersonDto(name = "Stanley Wilson", age = 43, active = true, companyId = companyTwoId))

                    val random = Random()
                    (0..100).forEach { i ->
                        add(
                            PersonDto(
                                name = (0..200).joinToString(),
                                age = random.nextInt(100),
                                active = true,
                                companyId = companyOneId
                            )
                        )
                    }
                }

                close()
            }
        }
    }
}