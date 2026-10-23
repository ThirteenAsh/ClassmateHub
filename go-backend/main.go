package main

import (
	"context"
	"database/sql"
	"errors"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"
)

func main() {
	if err := run(); err != nil {
		log.Fatal(err)
	}
}

func run() error {
	cfg, err := loadConfig()
	if err != nil {
		return err
	}
	db, err := sql.Open("mysql", cfg.dsn)
	if err != nil {
		return errors.New("无法初始化数据库连接")
	}
	defer db.Close()
	db.SetMaxOpenConns(5)
	db.SetMaxIdleConns(2)
	db.SetConnMaxLifetime(3 * time.Minute)
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	if err := db.PingContext(ctx); err != nil {
		return errors.New("数据库连接失败，请检查 DB_* 配置及云服务器的数据库访问权限")
	}
	// 只读核对，不在启动时重建或修改生产数据。
	if _, err := db.ExecContext(ctx, "SELECT id, user_id, clazz_id FROM student_profile LIMIT 0"); err != nil {
		return errors.New("数据库表不完整，请按 README 初始化数据库")
	}
	app := newApp(db, cfg)
	server := &http.Server{Addr: cfg.address, Handler: app.routes(), ReadHeaderTimeout: 5 * time.Second,
		ReadTimeout: 15 * time.Second, WriteTimeout: 20 * time.Second, IdleTimeout: 60 * time.Second}
	stopping, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()
	done := make(chan error, 1)
	go func() {
		log.Printf("ClassmateHub Go 后端已启动，端口 %s", cfg.address)
		done <- server.ListenAndServe()
	}()
	select {
	case err := <-done:
		if !errors.Is(err, http.ErrServerClosed) {
			return err
		}
	case <-stopping.Done():
		ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		return server.Shutdown(ctx)
	}
	return nil
}
