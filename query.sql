select * from users;

select * from merchants;

select * from products;

select * from orders;

select * from products
join merchants on products.id_merchant = merchants.id;

select * from orders_detail
left outer join public.orders o on orders_detail.id_order = o.id
left outer join public.products p on orders_detail.id_product = p.id;
