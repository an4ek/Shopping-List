package com.example.data.di;

import com.example.data.db.AppDatabase;
import com.example.data.db.dao.ShoppingItemDao;
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
public final class DatabaseModule_ProvideItemDaoFactory implements Factory<ShoppingItemDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideItemDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ShoppingItemDao get() {
    return provideItemDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideItemDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideItemDaoFactory(dbProvider);
  }

  public static ShoppingItemDao provideItemDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideItemDao(db));
  }
}
