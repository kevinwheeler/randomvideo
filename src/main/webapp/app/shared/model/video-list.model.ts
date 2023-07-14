import { IVideo } from 'app/shared/model/video.model';
import { IXUser } from 'app/shared/model/x-user.model';

export interface IVideoList {
  id?: number;
  name?: string;
  slug?: string;
  videos?: IVideo[] | null;
  xUser?: IXUser | null;
}

export const defaultValue: Readonly<IVideoList> = {};
