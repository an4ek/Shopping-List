package com.example.data.repository;

import com.example.data.db.dao.ShoppingItemDao;
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
public final class ShoppingItemRepositoryImpl_Factory implements Factory<ShoppingItemRepositoryImpl> {
  private final Provider<ShoppingItemDao> daoProvider;

  public ShoppingItemRepositoryImpl_Factory(Provider<ShoppingItemDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ShoppingItemRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static ShoppingItemRepositoryImpl_Factory create(Provider<ShoppingItemDao> daoProvider) {
    return new ShoppingItemRepositoryImpl_Factory(daoProvider);
  }

  public static ShoppingItemRepositoryImpl newInstance(ShoppingItemDao dao) {
    return new ShoppingItemRepositoryImpl(dao);
  }
}
