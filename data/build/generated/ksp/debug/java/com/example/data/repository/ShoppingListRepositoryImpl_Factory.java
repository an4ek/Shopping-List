package com.example.data.repository;

import com.example.data.db.dao.ShoppingListDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ShoppingListRepositoryImpl_Factory implements Factory<ShoppingListRepositoryImpl> {
  private final Provider<ShoppingListDao> daoProvider;

  public ShoppingListRepositoryImpl_Factory(Provider<ShoppingListDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ShoppingListRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static ShoppingListRepositoryImpl_Factory create(Provider<ShoppingListDao> daoProvider) {
    return new ShoppingListRepositoryImpl_Factory(daoProvider);
  }

  public static ShoppingListRepositoryImpl newInstance(ShoppingListDao dao) {
    return new ShoppingListRepositoryImpl(dao);
  }
}
