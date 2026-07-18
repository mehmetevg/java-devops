pipeline {
    agent any
    stages {
        stage('Eski Konteynerleri Temizle') {
            steps {
                echo 'Sistemdeki eski staj_app temizleniyor...'
                sh 'docker stop staj_app || true'
                sh 'docker rm staj_app || true'
            }
        }
        stage('GitHub\'dan Kodu Çek') {
            steps {
                // SCM'de tanımladığımız ayarlardan kodu otomatik çeker
                checkout scm
            }
        }
        stage('Docker İmajını Derle') {
            steps {
                echo 'Yeni Docker imajı build ediliyor...'
                sh 'docker build -t staj_uygulamasi-app:latest .'
            }
        }
        stage('Uygulamayı Ayağa Kaldır') {
            steps {
                echo 'Sistem güncelleniyor ve başlatılıyor...'
                sh 'docker-compose up -d --build --no-deps staj_app'
            }
        }
    }
}
