import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class PasswordResetInitService {

    constructor(private http: Http) {}

    save(mail: string): Observable<any> {
        return this.http.post('jhipsteruaa/api/account/reset-password/init', mail);
    }
}
