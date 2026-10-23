package main

import (
	"crypto/rand"
	"encoding/hex"
	"net/http"
	"strings"
	"sync"
	"time"
)

type session struct {
	userID  int64
	expires time.Time
}
type sessionStore struct {
	mu      sync.Mutex
	entries map[string]session
	timeout time.Duration
}

func newSessionStore(timeout time.Duration) *sessionStore {
	return &sessionStore{entries: map[string]session{}, timeout: timeout}
}

func (s *sessionStore) issue(userID int64) (string, time.Time, error) {
	s.mu.Lock()
	defer s.mu.Unlock()
	now := time.Now()
	for token, existing := range s.entries {
		if !now.Before(existing.expires) {
			delete(s.entries, token)
			continue
		}
		// Sa-Token 默认允许同时登录并复用有效 token。
		if existing.userID == userID {
			return token, existing.expires, nil
		}
	}
	var bytes [32]byte
	if _, err := rand.Read(bytes[:]); err != nil {
		return "", time.Time{}, err
	}
	token := hex.EncodeToString(bytes[:])
	expires := now.Add(s.timeout)
	s.entries[token] = session{userID, expires}
	return token, expires, nil
}

func (s *sessionStore) lookup(token string) (int64, bool) {
	s.mu.Lock()
	defer s.mu.Unlock()
	entry, ok := s.entries[token]
	if !ok {
		return 0, false
	}
	if !time.Now().Before(entry.expires) {
		delete(s.entries, token)
		return 0, false
	}
	return entry.userID, true
}

func (s *sessionStore) revoke(userID int64) {
	s.mu.Lock()
	defer s.mu.Unlock()
	for token, entry := range s.entries {
		if entry.userID == userID {
			delete(s.entries, token)
		}
	}
}

func requestToken(r *http.Request) string {
	if auth := r.Header.Get("Authorization"); auth != "" {
		parts := strings.Fields(auth)
		if len(parts) == 2 && strings.EqualFold(parts[0], "Bearer") {
			return parts[1]
		}
		return ""
	}
	if token := r.Header.Get("satoken"); token != "" {
		return token
	}
	if token := r.URL.Query().Get("satoken"); token != "" {
		return token
	}
	if cookie, err := r.Cookie("satoken"); err == nil {
		return cookie.Value
	}
	return ""
}

func (a *app) setCookie(w http.ResponseWriter, token string, expires time.Time) {
	maxAge := int(time.Until(expires).Seconds())
	if maxAge < 1 {
		maxAge = 1
	}
	http.SetCookie(w, &http.Cookie{Name: "satoken", Value: token, Path: "/", HttpOnly: true,
		Secure: a.cfg.cookieSecure, SameSite: http.SameSiteLaxMode, MaxAge: maxAge, Expires: expires})
}
