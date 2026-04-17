package com.example.data.di;

import com.example.data.db.AppDatabase;
import com.example.data.db.dao.ShoppingListDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DatabaseModule_ProvideListDaoFactory implements Factory<ShoppingListDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideListDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ShoppingListDao get() {
    return provideListDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideListDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideListDaoFactory(dbProvider);
  }

  public static ShoppingListDao provideListDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideListDao(db));
  }
}
