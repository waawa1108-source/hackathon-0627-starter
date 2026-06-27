package com.youtrust.hackathon;

import java.util.EnumMap;
import java.util.Map;

/**
 * 認証手段からプロバイダを引く登録簿。
 *
 * 新プロバイダの有効化は register(...) を1回呼ぶだけ。
 * if / switch による分岐を増やさないので、手段が増えても本体ロジックは膨らまない。
 */
public final class IdentityProviderRegistry {

    private final Map<AuthMethod, IdentityProvider> providers = new EnumMap<>(AuthMethod.class);

    public void register(IdentityProvider provider) {
        providers.put(provider.method(), provider);
    }

    public IdentityProvider resolve(AuthMethod method) {
        IdentityProvider provider = providers.get(method);
        if (provider == null) {
            throw new ValidationException("未対応の登録方法です: " + method);
        }
        return provider;
    }
}
