
import style from "./style";

export default function view(uid: string, props?: any): string {
    props = props || {};
    return `
            <div id="${uid}" class="">
                ${ style(uid, props) }
   
                <h1>PAGE 2</h1>
                        
            </div>

        `;
}