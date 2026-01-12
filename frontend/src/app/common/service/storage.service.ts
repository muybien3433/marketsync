import { Injectable } from '@angular/core';

export type StorageScope = 'local' | 'session';

@Injectable({ providedIn: 'root' })
export class StorageService {
    private readonly prefix = 'muybien.';

    set<T>(key: string, value: T, scope: StorageScope = 'local'): void {
        const storage = this.getStorage(scope);
        const fullKey = this.withPrefix(key);
        storage.setItem(fullKey, this.serialize(value));
    }

    get<T>(key: string, scope?: StorageScope): T | null {
        const fullKey = this.withPrefix(key);

        if (scope) {
            const storage = this.getStorage(scope);
            const raw = storage.getItem(fullKey);
            return this.deserialize<T>(raw);
        }

        const localRaw = localStorage.getItem(fullKey);
        if (localRaw !== null) return this.deserialize<T>(localRaw);

        const sessionRaw = sessionStorage.getItem(fullKey);
        if (sessionRaw !== null) return this.deserialize<T>(sessionRaw);

        return null;
    }

    getString(key: string, scope?: StorageScope): string | null {
        const fullKey = this.withPrefix(key);

        if (scope) {
            return this.getStorage(scope).getItem(fullKey);
        }

        return localStorage.getItem(fullKey) ?? sessionStorage.getItem(fullKey);
    }

    remove(key: string, scope?: StorageScope): void {
        const fullKey = this.withPrefix(key);

        if (scope) {
            this.getStorage(scope).removeItem(fullKey);
            return;
        }

        localStorage.removeItem(fullKey);
        sessionStorage.removeItem(fullKey);
    }

    exists(key: string, scope?: StorageScope): boolean {
        return this.get(key, scope) !== null;
    }

    clear(scope?: StorageScope): void {
        if (scope) {
            this.getStorage(scope).clear();
            return;
        }

        localStorage.clear();
        sessionStorage.clear();
    }

    private getStorage(scope: StorageScope): Storage {
        return scope === 'local' ? localStorage : sessionStorage;
    }

    private withPrefix(key: string): string {
        return `${this.prefix}${key}`;
    }

    private serialize<T>(value: T): string {
        if (value === null || value === undefined) return '';
        if (typeof value === 'string') return value;
        return JSON.stringify(value);
    }

    private deserialize<T>(raw: string | null): T | null {
        if (raw === null) return null;
        if (raw === '') return null;

        try {
            return JSON.parse(raw) as T;
        } catch {
            return raw as unknown as T;
        }
    }
}
