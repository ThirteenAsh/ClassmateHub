package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"strconv"
	"strings"
	"time"
	_ "time/tzdata"

	"github.com/go-sql-driver/mysql"
)

type config struct {
	address      string
	dsn          string
	location     *time.Location
	tokenTimeout time.Duration
	cookieSecure bool
}

// 环境变量优先于 .env；启动脚本会切换到 go-backend 目录。
func loadConfig() (config, error) {
	if err := loadDotEnv(".env"); err != nil {
		return config{}, err
	}
	location, err := time.LoadLocation(env("TZ", "Asia/Shanghai"))
	if err != nil {
		return config{}, fmt.Errorf("TZ 配置无效: %w", err)
	}
	timeout, err := strconv.ParseInt(env("TOKEN_TIMEOUT", "2592000"), 10, 64)
	if err != nil || timeout <= 0 || timeout > int64((3650*24*time.Hour)/time.Second) {
		return config{}, fmt.Errorf("TOKEN_TIMEOUT 必须为正整数秒，且不超过十年")
	}
	secure, err := strconv.ParseBool(env("COOKIE_SECURE", "false"))
	if err != nil {
		return config{}, fmt.Errorf("COOKIE_SECURE 必须为 true 或 false")
	}
	db := mysql.NewConfig()
	if raw := os.Getenv("DB_DSN"); raw != "" {
		db, err = mysql.ParseDSN(raw)
		if err != nil {
			return config{}, fmt.Errorf("DB_DSN 格式无效")
		}
	} else {
		db.User = env("DB_USER", "root")
		db.Passwd = env("DB_PASSWORD", "123456")
		db.Net = "tcp"
		db.Addr = net.JoinHostPort(env("DB_HOST", "127.0.0.1"), env("DB_PORT", "3306"))
		db.DBName = env("DB_NAME", "classmatehub")
	}
	if db.DBName == "" {
		return config{}, fmt.Errorf("必须指定数据库名称")
	}
	db.ParseTime = true
	db.Loc = location
	db.ClientFoundRows = true // 与 MySQL JDBC 一致：重复保存也返回成功。
	if db.Params == nil {
		db.Params = map[string]string{}
	}
	if _, ok := db.Params["charset"]; !ok {
		db.Params["charset"] = "utf8mb4"
	}
	db.Timeout = 5 * time.Second
	db.ReadTimeout = 10 * time.Second
	db.WriteTimeout = 10 * time.Second
	port, err := strconv.Atoi(env("PORT", "8080"))
	if err != nil || port < 1 || port > 65535 {
		return config{}, fmt.Errorf("PORT 必须为有效端口")
	}
	return config{address: ":" + strconv.Itoa(port), dsn: db.FormatDSN(), location: location,
		tokenTimeout: time.Duration(timeout) * time.Second, cookieSecure: secure}, nil
}

func env(key, fallback string) string {
	if value, ok := os.LookupEnv(key); ok {
		return value
	}
	return fallback
}

func loadDotEnv(path string) error {
	file, err := os.Open(path)
	if os.IsNotExist(err) {
		return nil
	}
	if err != nil {
		return err
	}
	defer file.Close()
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := strings.TrimSpace(strings.TrimPrefix(scanner.Text(), "\ufeff"))
		if line == "" || strings.HasPrefix(line, "#") {
			continue
		}
		key, value, ok := strings.Cut(line, "=")
		key = strings.TrimSpace(key)
		if !ok || key == "" {
			return fmt.Errorf(".env 包含无效的配置行")
		}
		value = strings.TrimSpace(value)
		if len(value) >= 2 && ((value[0] == '"' && value[len(value)-1] == '"') || (value[0] == '\'' && value[len(value)-1] == '\'')) {
			value = value[1 : len(value)-1]
		}
		if _, exists := os.LookupEnv(key); !exists {
			if err := os.Setenv(key, value); err != nil {
				return err
			}
		}
	}
	return scanner.Err()
}
