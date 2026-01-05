import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { itemApi, Item } from '../../api/itemApi';

const ItemListPage: React.FC = () => {
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const data = await itemApi.getSellerItems();
        setItems(data);
      } catch (err: any) {
        setError('Failed to load items. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchItems();
  }, []);

  if (loading) {
    return <div className="text-white text-center py-10">Loading items...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-white">My Items</h1>
        <Link 
          to="/seller/items/new" 
          className="bg-indigo-600 hover:bg-indigo-500 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        >
          Add New Item
        </Link>
      </div>

      {error && (
        <div className="bg-red-500/10 border border-red-500/50 text-red-400 p-3 rounded-lg mb-6 text-sm">
          {error}
        </div>
      )}

      {items.length === 0 ? (
        <div className="bg-slate-800 rounded-xl border border-slate-700 shadow-md p-10 text-center">
          <p className="text-slate-400 mb-4">You haven't added any items yet.</p>
          <Link 
            to="/seller/items/new" 
            className="text-indigo-400 hover:text-indigo-300 font-medium"
          >
            Create your first item →
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {items.map((item) => (
            <div key={item.id} className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden flex flex-col">
              <div className="h-48 bg-slate-700 flex items-center justify-center">
                <span className="text-slate-500">No Image</span>
              </div>
              <div className="p-5 flex-1 flex flex-col">
                <div className="flex justify-between items-start mb-2">
                  <h3 className="text-lg font-bold text-white truncate">{item.title}</h3>
                  <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                    item.status === 'AVAILABLE' ? 'bg-green-500/20 text-green-400' : 
                    item.status === 'IN_AUCTION' ? 'bg-blue-500/20 text-blue-400' : 
                    'bg-slate-500/20 text-slate-400'
                  }`}>
                    {item.status}
                  </span>
                </div>
                <p className="text-slate-400 text-sm mb-4 line-clamp-2 flex-1">{item.description}</p>
                
                <div className="flex justify-between items-center mt-auto pt-4 border-t border-slate-700">
                  <span className="text-indigo-400 font-bold">${item.startingPrice}</span>
                  <Link 
                    to={`/seller/items/${item.id}`}
                    className="text-sm text-slate-300 hover:text-white"
                  >
                    View Details
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ItemListPage;
